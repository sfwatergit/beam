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

import akka.actor.ActorRef;
import beam.playground.ajdeqsim.v1.logic.Vehicle;

/**
 * The basic EventMessage type.
 *
 * @author rashid_waraich
 */
public abstract class EventMessage extends Message {
	public Vehicle vehicle;
	public ActorRef scheduler;

	public EventMessage(ActorRef scheduler, Vehicle vehicle) {
		super();
		this.vehicle = vehicle;
		this.scheduler = scheduler;
	}

}
