package beam.playground.jdeqsimPerformance.akkaeventsim.subscribers;

import akka.actor.UntypedActor;
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
            updateStatistics(1);

            Event event = (Event)message;

            if(event.getEventType().equalsIgnoreCase(LinkEnterEvent.EVENT_TYPE)){
                LinkEnterEvent linkEnterEvent = (LinkEnterEvent)event;
                LinkEnterEventHandler handler = (LinkEnterEventHandler)eventHandler;
                handler.handleEvent(linkEnterEvent);
            }

            //System.out.println("Event Received [" + event + "]");
        }else if(message instanceof String){
            if(((String) message).equalsIgnoreCase("END")){

                Util.calculateRateOfEventsReceived(getSelf().path().toString(), firstEventReceivedTime, lastEventReceiptTime, noOfEventsReceived);
            }
            //getSender().tell();
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
