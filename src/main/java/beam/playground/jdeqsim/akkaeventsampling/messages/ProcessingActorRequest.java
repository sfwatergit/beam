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
    private long synStartTime;
    private long synEndTime;
    public ProcessingActorRequest(List<Event> eventList, String eventType) {
        this.eventList = new ArrayList<>(eventList);
        this.eventType = eventType;
    }

    public ProcessingActorRequest(Hashtable<String, List<Event>> eventsCollection, long synStartTime, long synEndTime) {
        this.eventsCollection = new Hashtable<>(eventsCollection);
        this.synStartTime = synStartTime;
        this.synEndTime = synEndTime;
    }

    public long getSynStartTime() {
        return synStartTime;
    }

    public long getSynEndTime() {
        return synEndTime;
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
