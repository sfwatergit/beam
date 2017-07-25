package beam.playground.jdeqsimPerformance.akkaeventsim;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorContext;
import akka.pattern.Patterns;
import akka.util.Timeout;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.handlers.LinkCountEventHandler;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.BufferedEventMessage;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.EndSimulationMessage;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.GetHandlerMessage;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.SimulationCompleteMessage;
import beam.playground.jdeqsimPerformance.akkaeventsim.subscribers.EventSubscriber;
import beam.playground.jdeqsimPerformance.akkaeventsim.util.PerformanceParameter;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.core.events.handler.EventHandler;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by asif on 6/17/2017.
 */
public class EventManagerActor extends UntypedActor {
    private static final String ALL_EVENTS = "ALL_EVENTS";
    private static UntypedActorContext eventManagerContext;
    private static Map<String, List<ActorRef>> eventSubscribers = new HashMap<>();
    private static Map<String, ActorRef> handlerActors = new HashMap<>();
    private static boolean _isCompleted = false;
    private static int _subscriberProcessed = 0;
    private PerformanceParameter performanceParameter = new PerformanceParameter();

    public static void addHandler(EventHandler eventHandler, String handlerName) {
        // use the eventmanager reference and
        // You will just add the it will accept eventHandler,
        ActorRef actorRef = eventManagerContext.actorOf(Props.create(EventSubscriber.class, eventHandler), handlerName);
        EventManagerActor.handlerActors.put(handlerName, actorRef);
        if (eventHandler instanceof LinkEnterEventHandler) {
            EventManagerActor.addSubscriber(actorRef, LinkEnterEvent.EVENT_TYPE);
        } else if (eventHandler instanceof LinkLeaveEventHandler) {
            EventManagerActor.addSubscriber(actorRef, LinkLeaveEvent.EVENT_TYPE);
        } else if (eventHandler instanceof LinkCountEventHandler) {
            EventManagerActor.addSubscriber(actorRef, ALL_EVENTS);
        }
    }

    public static EventHandler getEventHandler(String handlerName) {
        /*
        There should be two method, one method will give name to the handler, when we subscribe
        It will give back the handler from the handlers map,
        it has to be a static method

        it should call the
         */

        EventHandler handler = null;
        try {
            ActorRef subscriberActorRef = EventManagerActor.handlerActors.get(handlerName);
            Timeout timeout = new Timeout(Duration.create(5, "seconds"));
            scala.concurrent.Future<Object> future = Patterns.ask(subscriberActorRef, new GetHandlerMessage(), timeout);
            handler = (EventHandler) Await.result(future, timeout.duration());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return handler;
    }

    public static boolean isCompleted() {
        return EventManagerActor._isCompleted;
    }

    private static void addSubscriber(ActorRef actorRef, String eventType) {
        List<ActorRef> subscribers = EventManagerActor.eventSubscribers.get(eventType);
        if (subscribers != null) {
            subscribers.addAll(Arrays.asList(actorRef));
        }
        EventManagerActor.eventSubscribers.put(eventType, Arrays.asList(actorRef));
    }

    private static void checkSubscriberCompletion() {
        int subscriberProcessed = 0;
        EventManagerActor._subscriberProcessed = 0;
        for (ActorRef _actorRefs : EventManagerActor.handlerActors.values()) {
            try {
                Timeout timeout = new Timeout(Duration.create(5, "seconds"));
                scala.concurrent.Future<Object> future = Patterns.ask(_actorRefs, new SimulationCompleteMessage(), timeout);
                String isCompleted = (String) Await.result(future, timeout.duration());
                if (isCompleted.equalsIgnoreCase("TRUE")) {
                    subscriberProcessed++;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        EventManagerActor._subscriberProcessed = subscriberProcessed;
        if (EventManagerActor._subscriberProcessed == EventManagerActor.handlerActors.keySet().size()) {
            EventManagerActor._isCompleted = true;
        }
    }

    @Override
    public void preStart() throws Exception {
        eventManagerContext = getContext();
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof BufferedEventMessage) {
            handleEventList(message);
        }
        handleMessage(message);
    }

    private void handleMessage(Object message) {
        if (message instanceof EndSimulationMessage) {
            for (String key : eventSubscribers.keySet()) {
                for (ActorRef actorRef : eventSubscribers.get(key)) {
                    actorRef.tell(new EndSimulationMessage(), getSelf());
                }
            }
            this.performanceParameter.calculateRateOfEventsReceived(getSelf().path().toString());
            checkSubscriberCompletion();
        } else {
            unhandled(message);
        }
    }

    private void handleEventList(Object message) {
        List<Event> events = ((BufferedEventMessage) message).getEventList();
        this.performanceParameter.updateStatistics(events.size());
        for (Event event : events) {
            List<ActorRef> _actorRefs = eventSubscribers.get(event.getEventType());
            List<ActorRef> _allEventActorRefs = eventSubscribers.get(ALL_EVENTS);

            if (_actorRefs != null) {
                for (ActorRef subscriberActor : _actorRefs) {
                    subscriberActor.tell(event, getSelf());
                }
            }
            if (_allEventActorRefs != null) {
                for (ActorRef subscriberActor : _allEventActorRefs) {
                    subscriberActor.tell(event, getSelf());
                }
            }
        }
    }

}
