package beam.agentsim.agents.choice.mode

import beam.agentsim.agents.modalBehaviors.ModeChoiceCalculator
import beam.router.Modes.BeamMode.CAR
import beam.router.RoutingModel.EmbodiedBeamTrip
import beam.sim.BeamServices
import scalaz.Scalaz._
/**
  * BEAM
  */
class ModeChoiceDriveIfAvailable(val beamServices: BeamServices) extends ModeChoiceCalculator {

  override def apply(alternatives: Vector[EmbodiedBeamTrip]) = {
    var containsDriveAlt: Vector[Int] = Vector[Int]()
    alternatives.zipWithIndex.foreach { alt =>
      if (alt._1.tripClassifier == CAR) {
        containsDriveAlt = containsDriveAlt :+ alt._2
      }
    }

    (if (containsDriveAlt.nonEmpty) {
      Some(containsDriveAlt.head)
    }
    else {
      chooseRandomAlternativeIndex(alternatives)
    }).map(x => alternatives(x))

  }
}
