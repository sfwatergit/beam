package beam.playground.jdeqsimPerformance.simpleeventsim;

import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.akkaeventsim.util.EventTimeComparator;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.PhysSimTimeSyncEvent;
import org.matsim.api.core.v01.events.Event;
import org.matsim.core.events.handler.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by asif on 6/17/2017.
 */
public class EventManager extends UntypedActor{

    List<EventHandler> handlers = new ArrayList<>();
    List<Event> events;

    int binSize = 100;
    double startTime = 0;
    double endTime = startTime + binSize;

    public EventManager(List<Event> events){

        this.events = events;
    }

    public void addHandler(EventHandler eventHandler){
        handlers.add(eventHandler);
    }

    public Set<Event> getEvents(){

        List<Event> _events = events.stream()
                .filter(event -> event.getTime() >= (startTime) && event.getTime() < endTime)
                .collect(Collectors.toList());

        Set eventSet = new TreeSet(new EventTimeComparator());
        eventSet.addAll(_events);
        return eventSet;
    }

    public void processEvents(Event physSimTimeSyncEvent){
        /**
         * 1. Retrieve the events from the queue
         * 2. Pass the events one by one to the all the event handlers using a loop
         * 3. Check the event type and only call the appropriate handler for it after checking handler instanceOf
         */

        Set<Event> events = getEvents();
        System.out.println("Event for bin [" + startTime + "," + endTime + ")");
        for(Event event : events){

            System.out.println("Event -> " + event.toString());
        }


        startTime = startTime + binSize;
        endTime = startTime + binSize;

    }



    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof PhysSimTimeSyncEvent) {

            PhysSimTimeSyncEvent physSimTimeSyncEvent = (PhysSimTimeSyncEvent)message;
            processEvents(physSimTimeSyncEvent);
        }else if(message instanceof List){
            List<Event> events = (List<Event>)message;
            System.out.println("Events received ->>> " + events.size());
            for(Event event : events) {
                System.out.println("Event -> " + event);
            }
        }
    }
}
