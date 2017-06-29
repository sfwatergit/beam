package beam.playground.jdeqsimPerformance.akkaeventsim;

import akka.actor.UntypedActor;
import beam.playground.exceptions.InvalidEventTime;
import org.matsim.api.core.v01.events.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by asif on 6/25/2017.
 */
public class EventBufferActor extends UntypedActor {

    Queue<Event> eventQueue = new PriorityQueue<>(100, new EventTimeComparator());

    PhysSimTimeSyncEvent physSimTimeSyncEvent = null;

    public EventBufferActor(){

    }


    public List<Event> getEvents(double timeThreshold){
        List<Event> events = new ArrayList<>();
        while(eventQueue.size() > 0){

            Event event = eventQueue.poll();
            if(timeThreshold > event.getTime()) {
                events.add(event);
            }else{
                break;
            }
        }
        return events;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof PhysSimTimeSyncEvent) {

            physSimTimeSyncEvent = (PhysSimTimeSyncEvent)message;
            List<Event> events = getEvents(physSimTimeSyncEvent.getTime());
            getContext().actorSelection("../EVENT_MANAGER").tell(events, getSelf());

        }else if(message instanceof Event){
            Event event = (Event)message;

            if(event.getTime() < physSimTimeSyncEvent.getTime()){
                throw new InvalidEventTime();
            }

            eventQueue.add(event);
        }
    }
}
