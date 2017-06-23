package beam.playground.jdeqsimPerformance.akkaeventsim;

import org.matsim.api.core.v01.events.Event;

import java.util.Comparator;

/**
 * Created by asif on 6/17/2017.
 */

public class EventTimeComparator implements Comparator<Event>
{
    @Override
    public int compare(Event x, Event y)
    {
        if (x.getTime() < y.getTime())
        {
            return -1;
        }
        if (x.getTime() > y.getTime())
        {
            return 1;
        }
        return 0;
    }
}