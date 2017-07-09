package beam.playground.jdeqsimPerformance.akkaeventsim.subscribers;

import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.simpleeventsim.Util;
import org.matsim.api.core.v01.events.Event;

/**
 * Created by asif on 7/9/2017.
 */
public class CountLinkEventSubscriber extends UntypedActor implements ISubscriber{

    /** PERFORMANCE NUMBERS */
    long noOfEventsReceived = 0;
    long firstEventReceivedTime = 0;
    long lastEventReceiptTime = 0;
    /** PERFORMANCE NUMBERS - END */

    int count = 0;



    @Override
    public void onReceive(Object message) throws Throwable {

        if(message instanceof Event){

            updateStatistics(1);

            count++;
        }else if(message instanceof String){
            if(((String) message).equalsIgnoreCase("END")){

                System.out.println(getSelf().path().toString() + " -> Events Received " + count);
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
