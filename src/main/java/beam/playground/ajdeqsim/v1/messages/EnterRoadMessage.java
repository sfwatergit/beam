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

import java.util.LinkedList;
import java.util.List;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.VehicleEntersTrafficEvent;
import org.matsim.core.mobsim.jdeqsim.JDEQSimConfigGroup;

import akka.actor.ActorRef;
import beam.playground.ajdeqsim.v1.logic.Road;
import beam.playground.ajdeqsim.v1.logic.Vehicle;

/**
 * The micro-simulation internal handler for entering a road.
 *
 * @author rashid_waraich
 */
public class EnterRoadMessage extends EventMessage {

	@Override
	public void handleMessage() {
		// enter the next road
		Road road = Road.getRoad(vehicle.getCurrentLinkId());
		road.enterRoad(this);
	}

	public EnterRoadMessage(ActorRef scheduler, Vehicle vehicle) {
		super(scheduler, vehicle);
		priority = JDEQSimConfigGroup.PRIORITY_ENTER_ROAD_MESSAGE;
	}

	@Override
	public List<Event> getEvents() {
		List<Event> events=new LinkedList();
		Event event = null;

		// the first EnterLink in a leg is a Wait2LinkEvent
		if (vehicle.getLinkIndex() == -1) {
			event = new VehicleEntersTrafficEvent(this.getMessageArrivalTime(), vehicle.getOwnerPerson().getId(), vehicle.getCurrentLinkId(), 
					Id.create(vehicle.getOwnerPerson().getId(), org.matsim.vehicles.Vehicle.class), null, 1.0);
		} else {
			event = new LinkEnterEvent(this.getMessageArrivalTime(), Id.create(vehicle.getOwnerPerson().getId(), org.matsim.vehicles.Vehicle.class), vehicle.getCurrentLinkId());
		}
		events.add(event);
		return events;
	}

}
