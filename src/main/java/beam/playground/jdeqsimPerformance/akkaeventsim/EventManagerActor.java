package beam.playground.jdeqsimPerformance.akkaeventsim;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorContext;
import akka.pattern.Patterns;
import akka.util.Timeout;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.handlers.LinkCountEventHandler;
import beam.playground.jdeqsimPerformance.akkaeventsim.subscribers.EventSubscriber;
import beam.playground.jdeqsimPerformance.akkaeventsim.util.Util;
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
public class EventManagerActor extends UntypedActor{
    private static final String ALL_EVENTS = "ALL_EVENTS";
    /**
     * PERFORMANCE NUMBERS - END
     */
    private static UntypedActorContext eventManagerContext;
    private static Map<String, List<ActorRef>> eventSubscribers = new HashMap<>();
    private static Map<String, ActorRef> handlerActors = new HashMap<>();
    private static boolean _isCompleted = false;
    private static int _subscriberProcessed = 0;
    /** PERFORMANCE NUMBERS */
    long noOfEventsReceived = 0;
    long firstEventReceivedTime = 0;
    long lastEventReceiptTime = 0;

    public EventManagerActor(){

    }

    public static void addHandler(EventHandler eventHandler){
        // use the eventmanager reference and
        // You will just add the it will accept eventHandler,
        // An other method, with a second argument, with a name for the handler, it will store, store the handler with the name in a map
        /*
        when we call the method with getHandler with the name argument.
         */

        ActorRef actorRef = eventManagerContext.actorOf(Props.create(EventSubscriber.class, eventHandler), "EventSubscriber" + EventManagerActor.eventSubscribers.size());
        if(eventHandler instanceof LinkEnterEventHandler) {

            EventManagerActor.addSubscriber2(actorRef, LinkEnterEvent.EVENT_TYPE);
        } else if (eventHandler instanceof LinkCountEventHandler) {

            EventManagerActor.addSubscriber2(actorRef, ALL_EVENTS);
        }
    }

    public static void addHandler(EventHandler eventHandler, String handlerName){
        // use the eventmanager reference and
        // You will just add the it will accept eventHandler,
        ActorRef actorRef = eventManagerContext.actorOf(Props.create(EventSubscriber.class, eventHandler), handlerName);
        EventManagerActor.handlerActors.put(handlerName, actorRef);

        if(eventHandler instanceof LinkEnterEventHandler) {

            EventManagerActor.addSubscriber2(actorRef, LinkEnterEvent.EVENT_TYPE);
        }else if(eventHandler instanceof LinkLeaveEventHandler) {

            EventManagerActor.addSubscriber2(actorRef, LinkLeaveEvent.EVENT_TYPE);
        } else if (eventHandler instanceof LinkCountEventHandler) {

            EventManagerActor.addSubscriber2(actorRef, ALL_EVENTS);
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
            scala.concurrent.Future<Object> future = Patterns.ask(subscriberActorRef, "GET_HANDLER", timeout);
            handler = (EventHandler) Await.result(future, timeout.duration());
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return handler;
    }

    public static void addSubscriber2(ActorRef actorRef, String eventType) {

        List<ActorRef> subscribers = EventManagerActor.eventSubscribers.get(eventType);
        if(subscribers != null){
            subscribers.addAll(Arrays.asList(actorRef));
        }
        EventManagerActor.eventSubscribers.put(eventType, Arrays.asList(actorRef));
    }

    public static void checkSubscriberCompletion(){
        int subscriberProcessed = 0;
        EventManagerActor._subscriberProcessed = 0;
        for (ActorRef _actorRefs : EventManagerActor.handlerActors.values()){

            try {
                //_actorRefs.tell("IS_COMPLETED", em.getSelf());
                Timeout timeout = new Timeout(Duration.create(5, "seconds"));
                scala.concurrent.Future<Object> future = Patterns.ask(_actorRefs, "IS_COMPLETED", timeout);
                String isCompleted = (String) Await.result(future, timeout.duration());
                if (isCompleted.equalsIgnoreCase("TRUE")) {
                    subscriberProcessed++;
                }
                //System.out.println(isCompleted + " iscompleted");
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        EventManagerActor._subscriberProcessed = subscriberProcessed;

        //System.out.println("em._subscriberProcessed == em.handlerActors.keySet().size() " + (em._subscriberProcessed == em.handlerActors.keySet().size()));
        if (EventManagerActor._subscriberProcessed == EventManagerActor.handlerActors.keySet().size()) {
            EventManagerActor._isCompleted = true;
        }
    }

    public static boolean isCompleted() {

        return EventManagerActor._isCompleted;
    }

    @Override
    public void preStart() throws Exception {
        eventManagerContext = getContext();
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof List) {

            handleEventList(message);
        } else if (message instanceof String) {

            handleMessage(message);
        }
    }

    public void handleMessage(Object message) {

        String _message = (String) message;
        if (_message.equals("SIM_COMPLETED")) {
            System.out.println("Sim completed received");
        } else if (_message.equals("END")) {

            for (String key : eventSubscribers.keySet()) {
                for (ActorRef actorRef : eventSubscribers.get(key)) {

                    actorRef.tell("END", getSelf());
                }
            }

            Util.calculateRateOfEventsReceived(getSelf().path().toString(), firstEventReceivedTime, lastEventReceiptTime, noOfEventsReceived);


            checkSubscriberCompletion();
        }
    }

    public void handleEventList(Object message){

        List<Event> events = (List<Event>)message;
        updateStatistics(events.size());
        for(Event event : events){
            List<ActorRef> _actorRefs = eventSubscribers.get(event.getEventType());

            if(_actorRefs != null) {
                for (ActorRef subscriberActor : _actorRefs) {
                    subscriberActor.tell(event, getSelf());
                }
            }
        }


        List<ActorRef> _actorRefs = eventSubscribers.get(ALL_EVENTS);
        for(Event event : events){

            if(_actorRefs != null) {
                for (ActorRef subscriberActor : _actorRefs) {
                    subscriberActor.tell(event, getSelf());
                }
            }
        }
    }

    public void updateStatistics(long receivedEvents){
        if(noOfEventsReceived == 0){
            firstEventReceivedTime = System.currentTimeMillis();
        }
        lastEventReceiptTime = System.currentTimeMillis();
        noOfEventsReceived += receivedEvents;
    }
}
