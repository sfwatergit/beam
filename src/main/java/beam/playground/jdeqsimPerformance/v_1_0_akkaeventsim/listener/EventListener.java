package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.listener;

import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.EventsBufferActor;
import org.matsim.api.core.v01.events.Event;

import java.util.List;

/**
 * Created by salma_000 on 7/6/2017.
 */
public class EventListener implements IEventListener {
    private static int callBackCount = 0;
    private static int receivedEventCount = 0;

    @Override
    public void callBack(List<Event> eventList) {
        callBackCount++;
        receivedEventCount = receivedEventCount + eventList.size();
        System.out.println("Buffer Actor Received EventCount " + EventsBufferActor.receivedEventCount);
        System.out.println("EventListener" + callBackCount);
        System.out.println("EventListener Received Count " + receivedEventCount);
        /*for(Event event:eventList){
            System.out.println(event.toString());
        }*/
    }
}
