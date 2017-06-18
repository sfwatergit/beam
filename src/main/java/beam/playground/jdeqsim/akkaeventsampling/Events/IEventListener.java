package beam.playground.jdeqsim.akkaeventsampling.Events;

import org.matsim.api.core.v01.events.Event;

import java.util.List;

public interface IEventListener {
    void callBack(List<Event> eventList);
}
