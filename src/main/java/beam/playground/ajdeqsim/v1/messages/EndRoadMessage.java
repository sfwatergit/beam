/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package beam.playground.ajdeqsim.v1.messages;

import java.util.List;

import org.matsim.api.core.v01.events.Event;

import akka.actor.ActorRef;
import beam.playground.ajdeqsim.v1.logic.Road;
import beam.playground.ajdeqsim.v1.logic.Vehicle;

/**
 * The micro-simulation internal handler, when the end of a road is reached.
 *
 * @author rashid_waraich
 */
public class EndRoadMessage extends EventMessage {

	@Override
	public void handleMessage() {
		if (vehicle.isCurrentLegFinished()) {
			/*
			 * the leg is completed, try to enter the last link but do not enter
			 * it (just wait, until you have clearance for enter and then leave
			 * the road)
			 */

			vehicle.initiateEndingLegMode();
			vehicle.moveToFirstLinkInNextLeg();
			Road road = Road.getRoad(vehicle.getCurrentLinkId());
			road.enterRequest(this);
		} else if (!vehicle.isCurrentLegFinished()) {
			// if leg is not finished yet
			vehicle.moveToNextLinkInLeg();

			Road nextRoad = Road.getRoad(vehicle.getCurrentLinkId());
			nextRoad.enterRequest(this);
		}
	}

	public EndRoadMessage(ActorRef scheduler, Vehicle vehicle) {
		super(scheduler, vehicle);
	}

	@Override
	public List<Event> getEvents() {
		return null;
	}

}
