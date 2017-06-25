package beam.playground.jdeqsimPerformance.akkaeventsim;

import akka.actor.UntypedActor;
import org.matsim.api.core.v01.events.Event;

import java.util.List;

/**
 * Created by asif on 6/17/2017.
 */
public class EventManagerActor extends UntypedActor{

    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof List){
            List<Event> events = (List<Event>)message;
            System.out.println("Events received ->>> " + events.size());
            for(Event event : events) {
                System.out.println("Event -> " + event);
            }
        }
    }
}
