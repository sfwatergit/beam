package beam.playground.jdeqsimPerformance.akkaeventsim;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;
import beam.playground.jdeqsimPerformance.akkaeventsim.handlers.LinkCountEventHandlerI;
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

import java.util.*;

/**
 * Created by asif on 6/17/2017.
 */
public class EventManagerActor extends UntypedActor{
    /** PERFORMANCE NUMBERS */
    long noOfEventsReceived = 0;
    long firstEventReceivedTime = 0;
    long lastEventReceiptTime = 0;
    /** PERFORMANCE NUMBERS - END */

    Map<String, List<ActorRef>> eventSubscribers = new HashMap<>();

    //List<ActorRef> eventSubscribers = new ArrayList<>();
    boolean _isCompleted = false;
    int _subscriberProcessed = 0;

    public static EventManagerActor em = null;

    public EventManagerActor(){

        /*if(em == null){
            em = new EventManagerActor();
        }*/

        em = this;
    }

    public static void addHandler(EventHandler eventHandler){
        // use the eventmanager reference and
        // You will just add the it will accept eventHandler,
        // An other method, with a second argument, with a name for the handler, it will store, store the handler with the name in a map
        /*
        when we call the method with getHandler with the name argument.
         */

        ActorRef actorRef = em.getContext().actorOf(Props.create(EventSubscriber.class, eventHandler), "EventSubscriber" + em.eventSubscribers.size());
        if(eventHandler instanceof LinkEnterEventHandler) {

            EventManagerActor.addSubscriber2(actorRef, LinkEnterEvent.EVENT_TYPE);
        }else if(eventHandler instanceof LinkCountEventHandlerI){

            EventManagerActor.addSubscriber2(actorRef, "ALL_EVENTS");
        }
    }

    Map<String, ActorRef> handlerActors = new HashMap<>();
    //Map<String, EventHandler> handlerActors = new HashMap<>();

    public static void addHandler(EventHandler eventHandler, String handlerName){
        // use the eventmanager reference and
        // You will just add the it will accept eventHandler,
        ActorRef actorRef = em.getContext().actorOf(Props.create(EventSubscriber.class, eventHandler), handlerName);
        em.handlerActors.put(handlerName, actorRef);

        if(eventHandler instanceof LinkEnterEventHandler) {

            EventManagerActor.addSubscriber2(actorRef, LinkEnterEvent.EVENT_TYPE);
        }else if(eventHandler instanceof LinkLeaveEventHandler) {

            EventManagerActor.addSubscriber2(actorRef, LinkLeaveEvent.EVENT_TYPE);
        }else if(eventHandler instanceof LinkCountEventHandlerI){

            EventManagerActor.addSubscriber2(actorRef, "ALL_EVENTS");
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
            ActorRef subscriberActorRef = em.handlerActors.get(handlerName);
            Timeout timeout = new Timeout(Duration.create(5, "seconds"));
            scala.concurrent.Future<Object> future = Patterns.ask(subscriberActorRef, "GET_HANDLER", timeout);
            handler = (EventHandler) Await.result(future, timeout.duration());
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return handler;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof List){

            handleEventList(message);
        }else if(message instanceof String){

            handleMessage(message);
        }
    }


    /*public void addSubscriber(Object message){

        SubscribeMessage subscribeMessage = (SubscribeMessage)message;
        for(String eventType : subscribeMessage.getEventTypes()){

            List<ActorRef> subscribers = eventSubscribers.get(eventType);
            if(subscribers != null){
                subscribers.addAll(subscribeMessage.getSubscribers());
            }
            eventSubscribers.put(eventType, subscribeMessage.getSubscribers());
        }

        System.out.println("eventSubscribers " + eventSubscribers);

    }*/

    public static void addSubscriber2(ActorRef actorRef, String eventType){

        List<ActorRef> subscribers = em.eventSubscribers.get(eventType);
        if(subscribers != null){
            subscribers.addAll(Arrays.asList(actorRef));
        }
        em.eventSubscribers.put(eventType, Arrays.asList(actorRef));
    }



    public void handleMessage(Object message){

        String _message = (String)message;
        if(_message.equals("SIM_COMPLETED")){
            System.out.println("Sim completed received");
            //eventHandler.getCSVWriter().printLinkDataToCSV();
            //eventHandler.getCSVWriter().printLinkData();
        }else if(_message.equals("END")){

            for(String key : eventSubscribers.keySet()){
                for(ActorRef actorRef : eventSubscribers.get(key)){

                    actorRef.tell("END", getSelf());
                }
            }

            Util.calculateRateOfEventsReceived(getSelf().path().toString(), firstEventReceivedTime, lastEventReceiptTime, noOfEventsReceived);


            checkSubscriberCompletion();
        }
    }

    public static void checkSubscriberCompletion(){
        int subscriberProcessed = 0;
        em._subscriberProcessed = 0;
        for(ActorRef _actorRefs : em.handlerActors.values()){

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
        em._subscriberProcessed = subscriberProcessed;

        //System.out.println("em._subscriberProcessed == em.handlerActors.keySet().size() " + (em._subscriberProcessed == em.handlerActors.keySet().size()));
        if(em._subscriberProcessed == em.handlerActors.keySet().size()){
            em._isCompleted = true;
        }
    }


    public static boolean isCompleted(){

        return em._isCompleted;
    }



    public void handleEventList(Object message){

        List<Event> events = (List<Event>)message;
        updateStatistics(events.size());

        //List<ActorRef> actorRefs = eventSubscribers.get("ALL");
        for(Event event : events){
            List<ActorRef> _actorRefs = eventSubscribers.get(event.getEventType());

            if(_actorRefs != null) {
                for (ActorRef subscriberActor : _actorRefs) {
                    subscriberActor.tell(event, getSelf());
                }
            }
        }


        List<ActorRef> _actorRefs = eventSubscribers.get("ALL_EVENTS");
        for(Event event : events){

            if(_actorRefs != null) {
                for (ActorRef subscriberActor : _actorRefs) {
                    subscriberActor.tell(event, getSelf());
                }
            }
        }

        //System.out.println("Events received ->>> " + events.size() + ", total events received " + totalEventsReceived + " current time " + currentTime);
        /*for(EventHandler eventHandler: eventHandlers) {
            for(Event event : events) {
                if(eventHandler instanceof LinkEnterEventHandlerImpl && event instanceof LinkEnterEvent) {
                    LinkEnterEvent _event = (LinkEnterEvent)event;
                    LinkEnterEventHandlerImpl _eventHandler = (LinkEnterEventHandlerImpl)eventHandler;
                    _eventHandler.handleEvent(_event);
                }
                //System.out.println("Event -> " + event);
            }
        }*/
    }

    public void updateStatistics(long receivedEvents){
        if(noOfEventsReceived == 0){
            firstEventReceivedTime = System.currentTimeMillis();
        }
        lastEventReceiptTime = System.currentTimeMillis();
        noOfEventsReceived += receivedEvents;
    }
}
