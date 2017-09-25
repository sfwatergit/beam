package beam.agentsim.agents.choice.mode

import beam.router.Modes.BeamMode
import beam.router.Modes.BeamMode.{BUS, FERRY, RAIL, SUBWAY, TRANSIT}
import beam.router.RoutingModel.EmbodiedBeamTrip
import org.matsim.api.core.v01.Id

/**
  * TransitFareDefaults
  *
  * If no fare is found, these defaults can be use.
  */
object TransitFareDefaults {

  def estimateTransitFaresIfNone(alternatives: Vector[EmbodiedBeamTrip]): Vector[BigDecimal] = {
    alternatives.map{ alt =>
      alt.tripClassifier match {
        case TRANSIT if alt.costEstimate == 0.0 =>
          var vehId = Id.createVehicleId("dummy")
          BigDecimal(alt.legs.map{ leg =>
            if(leg.beamVehicleId != vehId && faresByMode.contains(leg.beamLeg.mode) ){
              faresByMode.get(leg.beamLeg.mode).get
            }else{
              0
            }
          }.sum)
        case _ =>
          BigDecimal(0)
      }
    }
  }
  // USD per boarding
  // Source: http://www.vitalsigns.mtc.ca.gov/transit-cost-effectiveness
  val faresByMode = Map[BeamMode, Double](
    BUS -> 0.99,
    SUBWAY -> 3.43,
    FERRY -> 6.87,
    RAIL -> 4.52
  )

}
