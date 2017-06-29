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

package beam.playground.ajdeqsim.v1.logic;

import akka.actor.ActorRef;
import beam.playground.ajdeqsim.v1.messages.Message;
import beam.playground.ajdeqsim.v1.scheduler.TriggerMessageWithPayload;

/**
 * The basic building block for all simulation units.
 *
 * @author rashid_waraich
 */
public abstract class SimUnit {

	protected ActorRef scheduler = null;

	public SimUnit(ActorRef scheduler) {
		this.scheduler = scheduler;
	}

	public void sendMessage(Message m, SimUnit targetUnit, double messageArrivalTime) {
		m.setSendingUnit(this);
		m.setReceivingUnit(targetUnit);
		m.setMessageArrivalTime(messageArrivalTime);
		scheduler.tell(new TriggerMessageWithPayload(((Road) m.getReceivingUnit()).getRoadActorRef(),messageArrivalTime,m.getPriority(),m), null);
	}

}
