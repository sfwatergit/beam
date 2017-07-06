package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages;

import org.matsim.api.core.v01.events.Event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by salma_000 on 7/4/2017.
 */
public class BufferEventMessage implements Serializable {
    private Event event;
    private List<Event> eventList;
    private String eventType;
    public BufferEventMessage(Event event) {
        this.event = event;
    }

    public BufferEventMessage(List<Event> eventList, String eventType) {
        this.eventList = new ArrayList<>(eventList);
        this.eventType = eventType;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public String getEventType() {
        return eventType;
    }

    public Event getEvent() {
        return event;
    }
}
