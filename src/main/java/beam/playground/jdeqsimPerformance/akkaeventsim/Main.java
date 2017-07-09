package beam.playground.jdeqsimPerformance.akkaeventsim;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import beam.playground.jdeqsimPerformance.akkaeventsim.generators.RandomEventGenerator;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.PhysSimTimeSyncEvent;

import java.util.Calendar;

/**
 * Created by asif on 6/17/2017.
 */
public class Main {

    public static void main(String args[]){

        /**
         * Create an RandomEventGenerator instance and generate events and put them in a Queue
         * Create a scheduler that will create PhysSimTimeSyncEvent after a certain interval
         * Create an EventManager that will receive the PhysSimTimeSyncEvent
         *      1. it will pull the events from the Queue for a certain time bin
         *      2. It will run a loop and will notify specific type of Handler about a new event
         * Create a Handler and add it to the EventManager
         *
         *
         * Actors
         *      1. The handler can be an Actor that will process the event when it receives a message
         *      2. The EventManager can be an Actor that will receive the PhysSimTimeSyncEvent message
         *          and then start the processing of a specific set of events from the EventsQueue
         */
        RandomEventGenerator eg = new RandomEventGenerator();
        eg.generateEvents();

        ActorSystem system = ActorSystem.create("akkaeventsim");
        ActorRef eventManagerActor = system.actorOf(Props.create(EventManager.class, eg.allGeneratedEvents), "EVENT_MANAGER");

        PhysSimTimeSyncEvent signal = new PhysSimTimeSyncEvent(Calendar.getInstance().getTimeInMillis());

        eventManagerActor.tell(signal, ActorRef.noSender());
        eventManagerActor.tell(signal, ActorRef.noSender());
        eventManagerActor.tell(signal, ActorRef.noSender());
        eventManagerActor.tell(signal, ActorRef.noSender());
        eventManagerActor.tell(signal, ActorRef.noSender());
        eventManagerActor.tell(signal, ActorRef.noSender());
        eventManagerActor.tell(signal, ActorRef.noSender());
        eventManagerActor.tell(signal, ActorRef.noSender());
        eventManagerActor.tell(signal, ActorRef.noSender());
        eventManagerActor.tell(signal, ActorRef.noSender());
    }
}
