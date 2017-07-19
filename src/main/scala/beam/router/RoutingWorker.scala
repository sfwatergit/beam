package beam.router

import akka.actor.{Actor, ActorLogging, Props}
import beam.agentsim.agents.PersonAgent
import beam.router.BeamRouter._
import beam.router.Modes.BeamMode
import beam.router.RoutingModel.BeamTime
import beam.sim.{BeamServices, HasServices}
import org.matsim.api.core.v01.Id
import org.matsim.api.core.v01.population.Person
import org.matsim.facilities.Facility

trait RoutingWorker extends Actor with ActorLogging with HasServices {
  override final def receive: Receive = {
    case InitializeRouter =>
      log.info("Initializing Router")
      init
      context.parent ! RouterInitialized
      sender() ! RouterInitialized
    case RoutingRequest(fromFacility, toFacility, departureTime, accessMode, personId) =>
          //      log.info(s"Router received routing request from person $personId ($sender)")
          sender() ! calcRoute(fromFacility, toFacility, departureTime, accessMode, getPerson(personId))
    case msg =>
      log.info(s"Unknown message received by Router $msg")
  }

  def calcRoute(fromFacility: Facility[_], toFacility: Facility[_], departureTime: BeamTime, accessMode: Vector[BeamMode], person: Person): RoutingResponse

  def init

  protected def getPerson(personId: Id[PersonAgent]): Person = services.matsimServices.getScenario.getPopulation.getPersons.get(personId)
}

object RoutingWorker {
  trait HasProps {
    def props(beamServices: BeamServices): Props
  }

  def getRouterProps(routerClass: String, services: BeamServices): Props = {
    val runtimeMirror = scala.reflect.runtime.universe.runtimeMirror(getClass.getClassLoader)
    val module = runtimeMirror.staticModule(routerClass)
    val obj = runtimeMirror.reflectModule(module)
    val routerObject:HasProps = obj.instance.asInstanceOf[HasProps]
    routerObject.props(services)
  }
}


