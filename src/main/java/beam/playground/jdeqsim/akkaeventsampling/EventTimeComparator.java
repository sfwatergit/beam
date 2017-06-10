package beam.playground.jdeqsim.akkaeventsampling;

import org.matsim.api.core.v01.events.Event;

import java.util.Comparator;

public class EventTimeComparator implements Comparator<Event> {
    @Override
    public int compare(Event o1, Event o2) {

        if (o1.getTime() == o2.getTime())
            return 0;
        else if (o1.getTime() > o2.getTime())
            return 1;
        else
            return -1;
    }
}
