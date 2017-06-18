package beam.playground.jdeqsim.akkaeventsampling.messages;

import org.matsim.api.core.v01.events.Event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ProcessingActorRequest implements IRequest, Serializable {
    Hashtable<String, List<Event>> eventsCollection;
    private List<Event> eventList;
    private String eventType;

    public ProcessingActorRequest(List<Event> eventList, String eventType) {
        this.eventList = new ArrayList<>(eventList);
        this.eventType = eventType;
    }

    public ProcessingActorRequest(Hashtable<String, List<Event>> eventsCollection) {
        this.eventsCollection = new Hashtable<>(eventsCollection);
    }

    public Hashtable<String, List<Event>> getEventsCollection() {
        return eventsCollection;
    }

    public String getEventType() {
        return eventType;
    }

    public List<Event> getEventList() {
        return eventList;
    }
}
