package beam.playground.jdeqsimPerformance.akkaeventsim.subscribers;

import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.simpleeventsim.Util;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;

/**
 * Created by asif on 7/9/2017.
 */
public class EnterLinkEventSubscriber extends UntypedActor implements ISubscriber{
    /** PERFORMANCE NUMBERS */
    long noOfEventsReceived = 0;
    long firstEventReceivedTime = 0;
    long lastEventReceiptTime = 0;
    /** PERFORMANCE NUMBERS - END */

    @Override
    public void onReceive(Object message) throws Throwable {

        if(message instanceof Event){
            updateStatistics(1);

            LinkEnterEvent event = (LinkEnterEvent)message;
            //System.out.println("Event Received [" + event + "]");
        }else if(message instanceof String){
            if(((String) message).equalsIgnoreCase("END")){

                Util.calculateRateOfEventsReceived(getSelf().path().toString(), firstEventReceivedTime, lastEventReceiptTime, noOfEventsReceived);
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
