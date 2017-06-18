package beam.playground.jdeqsim.akkaeventsampling.messages;

import org.matsim.api.core.v01.events.Event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by salma_000 on 6/18/2017.
 */
public class NotifyEventSubscriber implements Serializable {
    Hashtable<String, List<Event>> eventsCollection;
    private List<Event> eventList;
    private String eventType;

    public NotifyEventSubscriber(List<Event> eventList, String eventType) {
        this.eventList = new ArrayList<>(eventList);
        this.eventType = eventType;
    }

    public NotifyEventSubscriber(Hashtable<String, List<Event>> eventsCollection) {
        this.eventsCollection = new Hashtable<>(eventsCollection);
    }

    public Hashtable<String, List<Event>> getEventsCollection() {
        return eventsCollection;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public String getEventType() {
        return eventType;
    }
}
