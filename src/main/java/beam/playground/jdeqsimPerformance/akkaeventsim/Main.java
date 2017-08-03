package beam.playground.jdeqsimPerformance.akkaeventsim;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.LinkEnterEventCountHandlerImpl;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.LinkEventCountHandlerImpl;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.LinkLeaveEventHandlerImpl;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.handlers.LinkCountEventHandler;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.handlers.LinkEnterEventCountHandler;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.handlers.LinkLeaveEventCountHandler;
import beam.playground.jdeqsimPerformance.akkaeventsim.generators.RealTimeEventGenerator;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.GenerateEventMessage;

/**
 * Created by asif on 6/17/2017.
 */
public class Main {

    public static void main(String args[]) {

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
        //eventGeneratorActor.tell("START", ActorRef.noSender());


        /*
        Here eventManagerActor.getHandler(handlerName) from the EventSubscriber
        and this should give back the eventHandler and then we will cast it to the specific handler
        at anytime we can retrieve the state of the event handler
        if we have the name of the event handler we must be able to retrieve the state at any point in the system.
         */


        String handlerName = "LinkEnterEventHandler";
        LinkEnterEventCountHandler handler = new LinkEnterEventCountHandlerImpl();
        EventManagerActor.addHandler(handler, handlerName);

        String handlerName2 = "LinkCountEventHandler";
        LinkCountEventHandler handler2 = new LinkEventCountHandlerImpl();
        EventManagerActor.addHandler(handler2, handlerName2);

        String handlerName3 = "LinkLeaveEventHandler";
        LinkLeaveEventCountHandler handler3 = new LinkLeaveEventHandlerImpl();
        EventManagerActor.addHandler(handler3, handlerName3);

        eventGeneratorActor.tell(new GenerateEventMessage(100000), ActorRef.noSender());

        //System.out.println("EventManagerActor.isCompleted() -> " + EventManagerActor.isCompleted());
        while (EventManagerActor.isCompleted() == false) {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        LinkEnterEventCountHandler _handler = (LinkEnterEventCountHandler) EventManagerActor.getEventHandler(handlerName);
        LinkCountEventHandler _handler2 = (LinkCountEventHandler) EventManagerActor.getEventHandler(handlerName2);
        LinkLeaveEventCountHandler _handler3 = (LinkLeaveEventCountHandler) EventManagerActor.getEventHandler(handlerName3);
        System.out.println(handlerName + " -> count -> " + _handler.getCount());
        System.out.println(handlerName2 + " -> count -> " + _handler2.getCount());
        System.out.println(handlerName3 + " -> count -> " + _handler3.getCount());
        /*
        Is there a way to shutdown the system in the main.
         */
        system.terminate();
    }
}
