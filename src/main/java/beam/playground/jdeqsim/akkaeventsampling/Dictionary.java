package beam.playground.jdeqsim.akkaeventsampling;

import org.matsim.api.core.v01.events.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Dictionary {

    public static List<Event> eventList = Collections.synchronizedList(new ArrayList<>());

}