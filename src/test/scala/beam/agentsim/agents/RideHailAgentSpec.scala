package beam.agentsim.agents

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestActorRef, TestFSMRef, TestKit}
import akka.util.Timeout
import beam.agentsim.Resource.{CheckInResource, RegisterResource}
import beam.agentsim.ResourceManager.NotifyVehicleResourceIdle
import beam.agentsim.agents.BeamAgent.Finish
import beam.agentsim.agents.PersonAgent.DrivingInterrupted
import beam.agentsim.agents.modalbehaviors.DrivesVehicle.StopDriving
import beam.agentsim.agents.ridehail.RideHailAgent
import beam.agentsim.agents.ridehail.RideHailAgent._
import beam.agentsim.agents.vehicles.EnergyEconomyAttributes.Powertrain
import beam.agentsim.agents.vehicles.{BeamVehicle, BeamVehicleType, PassengerSchedule, VehiclePersonId}
import beam.agentsim.events.{PathTraversalEvent, SpaceTime}
import beam.agentsim.infrastructure.ZonalParkingManagerSpec
import beam.agentsim.scheduler.BeamAgentScheduler.{CompletionNotice, ScheduleTrigger, SchedulerProps, StartSchedule}
import beam.agentsim.scheduler.Trigger.TriggerWithId
import beam.agentsim.scheduler.{BeamAgentScheduler, Trigger}
import beam.router.Modes.BeamMode
import beam.router.model.{BeamLeg, BeamPath}
import beam.router.r5.NetworkCoordinator
import beam.sim.BeamServices
import beam.sim.common.GeoUtilsImpl
import beam.sim.config.BeamConfig
import beam.utils.StuckFinder
import beam.utils.TestConfigUtils.testConfig
import com.typesafe.config.ConfigFactory
import org.matsim.api.core.v01.events._
import org.matsim.api.core.v01.population.Person
import org.matsim.api.core.v01.{Coord, Id}
import org.matsim.core.events.EventsManagerImpl
import org.matsim.core.events.handler.BasicEventHandler
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSpecLike}

import scala.collection.concurrent.TrieMap

class RideHailAgentSpec
    extends TestKit(
      ActorSystem(
        "RideHailAgentSpec",
        ConfigFactory.parseString("""
  akka.log-dead-letters = 10
  akka.actor.debug.fsm = true
  akka.loglevel = debug
  """).withFallback(testConfig("test/input/beamville/beam.conf"))
      )
    )
    with FunSpecLike
    with BeforeAndAfterAll
    with MockitoSugar
    with ImplicitSender {

  private implicit val timeout: Timeout = Timeout(60, TimeUnit.SECONDS)
  lazy val config = BeamConfig(system.settings.config)
  lazy val eventsManager = new EventsManagerImpl()

  private val vehicles = TrieMap[Id[BeamVehicle], BeamVehicle]()
  private val personRefs = TrieMap[Id[Person], ActorRef]()

  lazy val services: BeamServices = {
    val theServices = mock[BeamServices]
    when(theServices.beamConfig).thenReturn(config)
    when(theServices.vehicles).thenReturn(vehicles)
    when(theServices.personRefs).thenReturn(personRefs)
    val geo = new GeoUtilsImpl(theServices)
    when(theServices.geo).thenReturn(geo)
    theServices
  }
  private lazy val zonalParkingManager: ActorRef = ZonalParkingManagerSpec.mockZonalParkingManager(services)

  case class TestTrigger(tick: Int) extends Trigger

  private lazy val networkCoordinator = new NetworkCoordinator(config)

  describe("A RideHailAgent") {

    def moveTo30000(scheduler: ActorRef, rideHailAgent: ActorRef) = {
      expectMsgType[RegisterResource]

      scheduler ! ScheduleTrigger(InitializeTrigger(0), rideHailAgent)
      scheduler ! ScheduleTrigger(TestTrigger(28800), self)
      scheduler ! StartSchedule(0)
      expectMsgType[CheckInResource] // Idle agent is idle
      expectMsgType[PersonDepartureEvent] // Departs..
      expectMsgType[PersonEntersVehicleEvent] // ..enters vehicle

      val trigger = expectMsgType[TriggerWithId] // 28800
      scheduler ! ScheduleTrigger(TestTrigger(30000), self)
      val passengerSchedule = PassengerSchedule()
        .addLegs(
          Seq(
            BeamLeg(
              28800,
              BeamMode.CAR,
              10000,
              BeamPath(
                Vector(),
                Vector(),
                None,
                SpaceTime(0.0, 0.0, 28800),
                SpaceTime(0.0, 0.0, 38800),
                10000
              )
            ),
            BeamLeg(
              38800,
              BeamMode.CAR,
              10000,
              BeamPath(
                Vector(),
                Vector(),
                None,
                SpaceTime(0.0, 0.0, 38800),
                SpaceTime(0.0, 0.0, 48800),
                10000
              )
            )
          )
        )
        .addPassenger(
          VehiclePersonId(Id.createVehicleId(1), Id.createPersonId(1)),
          Seq(
            BeamLeg(
              38800,
              BeamMode.CAR,
              10000,
              BeamPath(
                Vector(),
                Vector(),
                None,
                SpaceTime(0.0, 0.0, 38800),
                SpaceTime(0.0, 0.0, 48800),
                10000
              )
            )
          )
        )
      personRefs.put(Id.createPersonId(1), self) // I will mock the passenger
      rideHailAgent ! Interrupt(Id.create("1", classOf[Interrupt]), 30000)
      expectMsgClass(classOf[InterruptedWhileIdle])
      //expectMsg(InterruptedWhileIdle(_,_))
      rideHailAgent ! ModifyPassengerSchedule(passengerSchedule)
      rideHailAgent ! Resume()
      val modifyPassengerScheduleAck = expectMsgType[ModifyPassengerScheduleAck]
      modifyPassengerScheduleAck.triggersToSchedule.foreach(scheduler ! _)
      expectMsgType[VehicleEntersTrafficEvent]
      scheduler ! CompletionNotice(trigger.triggerId)

      expectMsgType[TriggerWithId] // 30000
    }

    it("should drive around when I tell him to") {
      val vehicleId = Id.createVehicleId(1)
      val beamVehicle =
        new BeamVehicle(vehicleId, new Powertrain(0.0), None, BeamVehicleType.defaultCarBeamVehicleType)
      beamVehicle.registerResource(self)
      vehicles.put(vehicleId, beamVehicle)

      val scheduler = TestActorRef[BeamAgentScheduler](
        SchedulerProps(
          config,
          stopTick = 64800,
          maxWindow = 10,
          new StuckFinder(config.beam.debug.stuckAgentDetection)
        )
      )

      val rideHailAgent = TestFSMRef(
        new RideHailAgent(
          Id.create("1", classOf[RideHailAgent]),
          scheduler,
          beamVehicle,
          new Coord(0.0, 0.0),
          eventsManager,
          zonalParkingManager,
          services,
          networkCoordinator.transportNetwork
        )
      )

      var trigger = moveTo30000(scheduler, rideHailAgent)

      // Now I want to interrupt the agent, and it will say that for any point in time after 28800,
      // I can tell it whatever I want. Even though it is already 30000 for me.

      rideHailAgent ! Interrupt(Id.create("1", classOf[Interrupt]), 30000)
      val interruptedAt = expectMsgType[InterruptedAt]
      assert(interruptedAt.currentPassengerScheduleIndex == 0) // I know this agent hasn't picked up the passenger yet
      assert(rideHailAgent.stateName == DrivingInterrupted)
      expectNoMessage()
      // Still, I tell it to resume
      rideHailAgent ! Resume()
      scheduler ! ScheduleTrigger(TestTrigger(50000), self)
      scheduler ! CompletionNotice(trigger.triggerId)

//      expectMsgType[NotifyResourceIdle]

      expectMsgType[VehicleLeavesTrafficEvent]

      expectMsgType[PathTraversalEvent]
      expectMsgType[VehicleEntersTrafficEvent]

      trigger = expectMsgType[TriggerWithId] // NotifyLegStartTrigger
      scheduler ! CompletionNotice(trigger.triggerId)

      // expectMsgType[NotifyResourceIdle]
      expectMsgType[VehicleLeavesTrafficEvent]
      expectMsgType[PathTraversalEvent]
      expectMsgType[NotifyVehicleResourceIdle]
      //expectMsgType[NotifyVehicleResourceIdleReply]
//      expectMsgType[CheckInResource]

      trigger = expectMsgType[TriggerWithId] // NotifyLegEndTrigger
      scheduler ! CompletionNotice(trigger.triggerId)

      rideHailAgent ! NotifyVehicleResourceIdleReply(None, Vector[ScheduleTrigger]())

      trigger = expectMsgType[TriggerWithId] // 50000
      scheduler ! CompletionNotice(trigger.triggerId)

      rideHailAgent ! Finish
      expectMsgType[CompletionNotice]
    }

    it("should let me interrupt it and tell it to cancel its job") {
      val vehicleId = Id.createVehicleId(1)
      val beamVehicle =
        new BeamVehicle(
          vehicleId,
          new Powertrain(0.0), /*vehicle*/ None,
          BeamVehicleType.defaultCarBeamVehicleType
        )
      beamVehicle.registerResource(self)
      vehicles.put(vehicleId, beamVehicle)

      val scheduler = TestActorRef[BeamAgentScheduler](
        SchedulerProps(
          config,
          stopTick = 64800,
          maxWindow = 10,
          new StuckFinder(config.beam.debug.stuckAgentDetection)
        )
      )

      val rideHailAgent = TestFSMRef(
        new RideHailAgent(
          Id.create("1", classOf[RideHailAgent]),
          scheduler,
          beamVehicle,
          new Coord(0.0, 0.0),
          eventsManager,
          zonalParkingManager,
          services,
          networkCoordinator.transportNetwork
        )
      )

      var trigger = moveTo30000(scheduler, rideHailAgent)

      // Now I want to interrupt the agent, and it will say that for any point in time after 28800,
      // I can tell it whatever I want. Even though it is already 30000 for me.

      rideHailAgent ! Interrupt(Id.create("1", classOf[Interrupt]), 30000)
      val interruptedAt = expectMsgType[InterruptedAt]
      assert(interruptedAt.currentPassengerScheduleIndex == 0) // I know this agent hasn't picked up the passenger yet
      assert(rideHailAgent.stateName == DrivingInterrupted)
      expectNoMessage()
      // I tell it to do nothing instead
      rideHailAgent ! StopDriving(30000)
      assert(rideHailAgent.stateName == IdleInterrupted)

      rideHailAgent ! Resume() // That's the opposite of Interrupt(), not resume driving
      scheduler ! ScheduleTrigger(TestTrigger(50000), self)
      scheduler ! CompletionNotice(trigger.triggerId)

//      expectMsgType[NotifyResourceIdle]
      expectMsgType[VehicleLeavesTrafficEvent]

      expectMsgType[PathTraversalEvent]
//      expectMsgType[CheckInResource]

      expectMsgType[NotifyVehicleResourceIdle]

      trigger = expectMsgType[TriggerWithId] // 50000
      scheduler ! CompletionNotice(trigger.triggerId)

      rideHailAgent ! Finish
      expectMsgType[CompletionNotice]
    }

    it("won't let me cancel its job after it has picked up passengers") {
      val vehicleId = Id.createVehicleId(1)
      val beamVehicle =
        new BeamVehicle(
          vehicleId,
          new Powertrain(0.0), /*vehicle,*/ None,
          BeamVehicleType.defaultCarBeamVehicleType
        )
      beamVehicle.registerResource(self)
      vehicles.put(vehicleId, beamVehicle)

      val scheduler = TestActorRef[BeamAgentScheduler](
        SchedulerProps(
          config,
          stopTick = 64800,
          maxWindow = 10,
          new StuckFinder(config.beam.debug.stuckAgentDetection)
        )
      )

      val rideHailAgent = TestFSMRef(
        new RideHailAgent(
          Id.create("1", classOf[RideHailAgent]),
          scheduler,
          beamVehicle,
          new Coord(0.0, 0.0),
          eventsManager,
          zonalParkingManager,
          services,
          networkCoordinator.transportNetwork
        )
      )

      var trigger = moveTo30000(scheduler, rideHailAgent)
      scheduler ! ScheduleTrigger(TestTrigger(40000), self)
      scheduler ! CompletionNotice(trigger.triggerId)

//      expectMsgType[NotifyResourceIdle]
      expectMsgType[VehicleLeavesTrafficEvent]
      expectMsgType[PathTraversalEvent]
      expectMsgType[VehicleEntersTrafficEvent]

      trigger = expectMsgType[TriggerWithId] // 40000
      rideHailAgent ! Interrupt(Id.create("1", classOf[Interrupt]), 30000)
      val interruptedAt = expectMsgType[InterruptedAt]
      assert(interruptedAt.currentPassengerScheduleIndex == 1) // I know this agent has now picked up the passenger
      assert(rideHailAgent.stateName == DrivingInterrupted)
      expectNoMessage()
      // Don't StopDriving() here because we have a Passenger and we don't know how that works yet.
    }

  }

  override def beforeAll: Unit = {
    eventsManager.addHandler(new BasicEventHandler {
      override def handleEvent(event: Event): Unit = {
        self ! event
      }
    })
    networkCoordinator.loadNetwork()
  }

  override def afterAll: Unit = {
    shutdown()
  }

}
