package beam.playground.jdeqsimPerformance.akkaeventsim;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import beam.playground.jdeqsimPerformance.akkaeventsim.generators.RealTimeEventGenerator;
import beam.playground.jdeqsimPerformance.akkaeventsim.subscribers.CountLinkEventSubscriber;
import beam.playground.jdeqsimPerformance.akkaeventsim.subscribers.EnterLinkEventSubscriber;
import beam.playground.jdeqsimPerformance.akkaeventsim.subscribers.LeaveLinkEventSubscriber;
import beam.playground.jdeqsimPerformance.akkaeventsim.subscribers.SubscribeMessage;
import org.matsim.api.core.v01.events.LinkEnterEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        //RandomEventGenerator eg = new RandomEventGenerator();
        //eg.generateEvents();

        ActorSystem system = ActorSystem.create("akkaeventsim");
        ActorRef eventManagerActor = system.actorOf(Props.create(EventManagerActor.class), "EventManagerActor");
        ActorRef eventBufferActor = system.actorOf(Props.create(EventBufferActor.class, eventManagerActor), "EventBufferActor");
        ActorRef eventGeneratorActor = system.actorOf(Props.create(RealTimeEventGenerator.class, eventBufferActor), "EventGeneratorActor_RT");
        eventGeneratorActor.tell("START", ActorRef.noSender());

        // Adding the Subscribers to the EventManagerActor
        ActorRef enterLinkEventSubscriber = system.actorOf(Props.create(EnterLinkEventSubscriber.class), "EnterLinkEventSubscriber");
        ActorRef leaveLinkEventSubscriber = system.actorOf(Props.create(LeaveLinkEventSubscriber.class), "LeaveLinkEventSubscriber");
        ActorRef countLinkEventSubscriber = system.actorOf(Props.create(CountLinkEventSubscriber.class), "CountLinkEventSubscriber");


        List<ActorRef> actorRefs = new ArrayList<>();
        List<String> eventTypes = new ArrayList<>();
        actorRefs.add(enterLinkEventSubscriber);
        eventTypes.add(LinkEnterEvent.EVENT_TYPE);
        SubscribeMessage subscribeMessage = new SubscribeMessage(actorRefs, eventTypes);
        eventManagerActor.tell(subscribeMessage, ActorRef.noSender());


        actorRefs = Arrays.asList(countLinkEventSubscriber);
        eventTypes = Arrays.asList("ALL");
        subscribeMessage = new SubscribeMessage(actorRefs, eventTypes);
        eventManagerActor.tell(subscribeMessage, ActorRef.noSender());


        eventGeneratorActor.tell("GENERATE_EVENTS", ActorRef.noSender());
    }
}
