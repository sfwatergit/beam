package beam.router.r5

import akka.actor.{Actor, ActorLogging, Props}
import beam.physsim.model.CopyNetworkAndUpdateRoadTravelTimes
import beam.sim.BeamServices
import org.matsim.core.trafficmonitoring.TravelTimeCalculator

/**
  * Created by salma_000 on 8/25/2017.
  */
class TransportNetworkWorker(beamServices: BeamServices) extends Actor with ActorLogging {
  var services: BeamServices = beamServices

  override def receive: Receive = {
    case networkUpdateRequest: CopyNetworkAndUpdateRoadTravelTimes =>
      log.info("Received TravelTimeCalculator")
      R5RoutingWorker.updateTimes(networkUpdateRequest.getUpdateRoadTravelTimes.getTravelTimeCalculator)
      sender() ! "REPLACE_NETWORK"
    case msg => {
        log.info(s"Unknown message[$msg] received by UpdateTransportNetwork Actor.")
    }
  }
}
object TransportNetworkWorker {

  def getUpdateTransportNetworkProps(beamServices: BeamServices) = Props(classOf[TransportNetworkWorker], beamServices)

}