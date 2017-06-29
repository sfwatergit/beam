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

import java.util.HashMap;
import java.util.LinkedList;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.mobsim.jdeqsim.JDEQSimConfigGroup;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.utils.misc.Time;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import beam.playground.ajdeqsim.v1.messages.DeadlockPreventionMessage;
import beam.playground.ajdeqsim.v1.messages.EventMessage;
import beam.playground.ajdeqsim.v1.messages.LeaveRoadMessage;
import beam.playground.beamSimAkkaProtoType.GlobalLibAndConfig;
import beam.playground.beamSimAkkaProtoType.chargingInfrastructure.ChargingInfrastructureManager;
import beam.playground.physicalSimProtoType.oldJDEQSim.Scheduler;

/**
 * The road is simulated as an active agent, moving around vehicles.
 *
 * @author rashid_waraich
 */
public class Road extends SimUnit {


	ActorRef roadActorRef;
	
	public void setRoadActorRef(ActorRef roadActorRef){
		this.roadActorRef= roadActorRef;
	}
	
	public ActorRef getRoadActorRef(){
		return roadActorRef;
	}
	
	public Road(ActorRef scheduler, Link link, ActorSystem system,ActorRef messageHandlerActor) {
		super(scheduler);
		
		setRoadActorRef(system.actorOf(Props.create(RoadActor.class,scheduler,link,messageHandlerActor)));
		
	}

	/**
	 * this must be initialized before starting the simulation! mapping:
	 * key=linkId used to find a road corresponding to a link
	 */
	static HashMap<Id<Link>, Road> allRoads = null;

	public static HashMap<Id<Link>, Road> getAllRoads() {
		return allRoads;
	}

	public static void setAllRoads(HashMap<Id<Link>, Road> allRoads) {
		Road.allRoads = allRoads;
	}

	protected Link link;

	public void leaveRoad(EventMessage msg) {
		getRoadActorRef().tell(msg, null);
	}

	public void enterRoad(EventMessage msg) {
		getRoadActorRef().tell(msg, null);
	}

	public void enterRequest(EventMessage msg) {
		getRoadActorRef().tell(msg, null);
	}

//	public void giveBackPromisedSpaceToRoad() {
//		this.noOfCarsPromisedToEnterRoad--;
//	}
	
	
	public void handleDeadlockPreventionMessage(DeadlockPreventionMessage deadlockPreventionMessage){
		getRoadActorRef().tell(deadlockPreventionMessage, null);
	}

	public Link getLink() {
		return this.link;
	}

	public static Road getRoad(Id<Link> linkId) {
		return getAllRoads().get(linkId);
	}

}
