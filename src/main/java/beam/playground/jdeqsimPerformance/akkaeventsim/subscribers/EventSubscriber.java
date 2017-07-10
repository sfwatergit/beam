package beam.playground.jdeqsimPerformance.akkaeventsim.subscribers;

import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.akkaeventsim.handlers.LinkCountEventHandlerI;
import beam.playground.jdeqsimPerformance.simpleeventsim.Util;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.core.events.handler.EventHandler;

/**
 * Created by asif on 7/9/2017.
 */
public class EventSubscriber extends UntypedActor implements ISubscriber{
    /** PERFORMANCE NUMBERS */
    long noOfEventsReceived = 0;
    long firstEventReceivedTime = 0;
    long lastEventReceiptTime = 0;
    /** PERFORMANCE NUMBERS - END */

    EventHandler eventHandler = null;

    public EventSubscriber(EventHandler eventHandler){

        this.eventHandler = eventHandler;
    }

    public void getEventHandler(){
        // call back actorref
    }

    @Override
    public void onReceive(Object message) throws Throwable {

        if(message instanceof Event){

            handleEvent(message);
            //System.out.println("Event Received [" + event + "]");
        }else if(message instanceof String){
            handleMessage(message);
        }
    }

    public void handleEvent(Object message){
        updateStatistics(1);

        Event event = (Event)message;

        if(event.getEventType().equalsIgnoreCase(LinkEnterEvent.EVENT_TYPE)){
            LinkEnterEvent linkEnterEvent = (LinkEnterEvent)event;
            LinkEnterEventHandler handler = (LinkEnterEventHandler)eventHandler;
            handler.handleEvent(linkEnterEvent);
        }

        if(eventHandler instanceof LinkCountEventHandlerI){

            LinkCountEventHandlerI handler = (LinkCountEventHandlerI) eventHandler;
            handler.handleEvent(event);
        }
    }

    public void handleMessage(Object message){

        String msg = (String) message;
        if(msg.equalsIgnoreCase("END")){

            Util.calculateRateOfEventsReceived(getSelf().path().toString(), firstEventReceivedTime, lastEventReceiptTime, noOfEventsReceived);
        }else if(msg.equalsIgnoreCase("GET_HANDLER")){

            getSender().tell(eventHandler, getSelf());
        }else if(msg.equalsIgnoreCase("IS_COMPLETED")){
            getSender().tell("TRUE", getSelf());
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