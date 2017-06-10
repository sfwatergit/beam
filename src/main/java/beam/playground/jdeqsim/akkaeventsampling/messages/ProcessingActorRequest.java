package beam.playground.jdeqsim.akkaeventsampling.messages;

import org.matsim.api.core.v01.events.Event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProcessingActorRequest implements IRequest, Serializable {
    private List<Event> eventList;

    public ProcessingActorRequest(List<Event> eventList) {
        this.eventList = new ArrayList<>(eventList);
    }

    public List<Event> getEventList() {
        return eventList;
    }
}
