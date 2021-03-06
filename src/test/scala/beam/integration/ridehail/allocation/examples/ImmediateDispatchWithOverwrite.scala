package beam.integration.ridehail.allocation.examples

import beam.agentsim.agents.modalbehaviors.DrivesVehicle.StopDrivingIfNoPassengerOnBoardReply
import beam.agentsim.agents.ridehail.RideHailManager
import beam.agentsim.agents.ridehail.allocation._
import beam.router.model.RoutingModel.DiscreteTime
import com.typesafe.scalalogging.LazyLogging
import org.matsim.api.core.v01.Id
import org.matsim.api.core.v01.population.Person
import org.matsim.vehicles.Vehicle

/*
Idea: try to overwrite one ridehail reservation


 */
class ImmediateDispatchWithOverwrite(val rideHailManager: RideHailManager)
    extends RideHailResourceAllocationManager(rideHailManager)
    with LazyLogging {

  var bufferedRideHailRequest: Set[VehicleAllocationRequest] = Set()
  var reservationCompleted = false
  var overwriteAttemptStarted = false

  override def proposeVehicleAllocation(
    vehicleAllocationRequest: VehicleAllocationRequest
  ): VehicleAllocationResponse = {

    if (!reservationCompleted) {
      bufferedRideHailRequest += vehicleAllocationRequest
      logger.debug(
        "proposeVehicleAllocation buffered - personId: {}",
        vehicleAllocationRequest.request.customer.personId
      )
    }

    // just go with closest request
    // Some(VehicleAllocation(rideHailManager.getClosestIdleRideHailAgent(vehicleAllocationRequest.request.pickUpLocation, rideHailManager.radiusInMeters),vehicleAllocationRequest.request.))

    /*
    val rideHailAgentLocation = rideHailManager.getClosestIdleRideHailAgent(
      vehicleAllocationRequest.pickUpLocation,
      rideHailManager.radiusInMeters
    )

    rideHailAgentLocation match {
      case Some(rideHailLocation) =>
        Some(VehicleAllocation(rideHailLocation.vehicleId, rideHailLocation.currentLocation))
      case None => None
    }
     */
    rideHailManager
      .getClosestIdleVehiclesWithinRadius(
        vehicleAllocationRequest.request.pickUpLocation,
        rideHailManager.radiusInMeters
      )
      .headOption match {
      case Some(agentLocation) =>
        VehicleAllocation(agentLocation, None)
      case None =>
        NoVehicleAllocated
    }

  }

  override def handleRideCancellationReply(reply: StopDrivingIfNoPassengerOnBoardReply): Unit = {

    bufferedRideHailRequests.decreaseNumberOfOpenOverwriteRequests()

    if (reply.success) {
      // overwrite first ride of day

      val firstRequestOfDay = bufferedRideHailRequest.head

      val rhl = rideHailManager
        .getClosestIdleRideHailAgent(
          firstRequestOfDay.request.pickUpLocation,
          rideHailManager.radiusInMeters
        )
        .get

      val request = firstRequestOfDay.request.copy(
        departAt = DiscreteTime(bufferedRideHailRequests.getTick.toInt)
      )

      rideHailManager.createRoutingRequestsToCustomerAndDestination(
        request,
        rhl
      )

      logger.debug(
        " new vehicle assigned:{}, tick: {}",
        rhl.vehicleId,
        reply.tick
      )

      bufferedRideHailRequests.registerVehicleAsReplacementVehicle(rhl.vehicleId)

    } else {
      logger.debug(
        "reassignment failed"
      )
      bufferedRideHailRequests.tryClosingBufferedRideHailRequestWaive()

      bufferedRideHailRequest = Set()
      overwriteAttemptStarted = false
      reservationCompleted = false
    }

  }

  // TODO: define 3 state names to allow for proper transitioning

  override def updateVehicleAllocations(tick: Int, triggerId: Long): Unit = {
    // try to cancel first ride of day

    if (!overwriteAttemptStarted && reservationCompleted) {
      logger.debug(
        "trying to reassign vehicle to customer:{}, tick: {}",
        bufferedRideHailRequest.head.request.customer,
        tick
      )
      rideHailManager.attemptToCancelCurrentRideRequest(
        tick,
        bufferedRideHailRequest.head.request.requestId
      )
      logger.debug(
        "attempt finished, tick: {}",
        tick
      )
      bufferedRideHailRequests.increaseNumberOfOpenOverwriteRequests()
      overwriteAttemptStarted = true
    }

  }

  override def reservationCompletionNotice(personId: Id[Person], vehicleId: Id[Vehicle]): Unit = {
    logger.debug("reservationCompletionNotice - personId: {}, vehicleId: {}", personId, vehicleId)

    if (!reservationCompleted) {
      bufferedRideHailRequest = bufferedRideHailRequest.filter(_.request.customer.personId == personId)

      if (bufferedRideHailRequest.nonEmpty) {
        reservationCompleted = true

        logger.debug("reservationCompletionNotice: true - personId: {}", personId)
      } else {
        reservationCompleted = false
        bufferedRideHailRequest = Set()
      }

    }

  }

}
