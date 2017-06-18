package beam.playground.jdeqsim.akkaeventsampling;

import org.matsim.api.core.v01.events.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Dictionary {

    public static List<Event> eventList = Collections.synchronizedList(new ArrayList<>());
    /*public static PriorityQueue<Event> linkEnterEventQueue=new PriorityQueue<Event>(5, new EventTimeComparator());
    public static PriorityQueue<Event>  linkLeaveEventQueue = new PriorityQueue<Event>(5, new EventTimeComparator());
    public static PriorityQueue<Event>  genericEventQueue = new PriorityQueue<Event>(5, new EventTimeComparator());
*/
    public static List<Event> linkEnterEventList = Collections.synchronizedList(new ArrayList<>());
    public static List<Event> linkLeaveEventList = Collections.synchronizedList(new ArrayList<>());
    public static List<Event> genericEventList = Collections.synchronizedList(new ArrayList<>());

}