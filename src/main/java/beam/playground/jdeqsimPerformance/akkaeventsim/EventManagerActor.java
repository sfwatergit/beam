package beam.playground.jdeqsimPerformance.akkaeventsim;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.akkaeventsim.subscribers.SubscribeMessage;
import beam.playground.jdeqsimPerformance.simpleeventsim.Util;
import org.matsim.api.core.v01.events.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public EventManagerActor(){
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof List){

            handleEventList(message);
        }else if(message instanceof String){

            handleMessage(message);
        }else if(message instanceof SubscribeMessage){

            addSubscriber(message);
        }
    }


    public void addSubscriber(Object message){

        SubscribeMessage subscribeMessage = (SubscribeMessage)message;
        for(String eventType : subscribeMessage.getEventTypes()){

            List<ActorRef> subscribers = eventSubscribers.get(eventType);
            if(subscribers != null){
                subscribers.addAll(subscribeMessage.getSubscribers());
            }
            eventSubscribers.put(eventType, subscribeMessage.getSubscribers());
        }

        System.out.println("eventSubscribers " + eventSubscribers);

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
        }
    }

    public void handleEventList(Object message){

        List<Event> events = (List<Event>)message;
        updateStatistics(events.size());

        List<ActorRef> actorRefs = eventSubscribers.get("ALL");

        for(Event event : events){

            List<ActorRef> _actorRefs = eventSubscribers.get(event.getEventType());
            List<ActorRef> finalActorList = new ArrayList<>();

            if(actorRefs != null) {
                finalActorList.addAll(actorRefs);
            }

            if(_actorRefs != null) {
                finalActorList.addAll(_actorRefs);
            }

            if(!finalActorList.isEmpty()) {
                for (ActorRef subscriberActor : finalActorList) {

                    subscriberActor.tell(event, getSelf());
                }
            }
        }

        //System.out.println("Events received ->>> " + events.size() + ", total events received " + totalEventsReceived + " current time " + currentTime);
        /*for(EventHandler eventHandler: eventHandlers) {
            for(Event event : events) {
                if(eventHandler instanceof LinkEnterEventHandler && event instanceof LinkEnterEvent) {
                    LinkEnterEvent _event = (LinkEnterEvent)event;
                    LinkEnterEventHandler _eventHandler = (LinkEnterEventHandler)eventHandler;
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
