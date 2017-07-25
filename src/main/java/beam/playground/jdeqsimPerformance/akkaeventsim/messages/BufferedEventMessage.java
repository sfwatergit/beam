package beam.playground.jdeqsimPerformance.akkaeventsim.messages;

import org.matsim.api.core.v01.events.Event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by salma_000 on 7/25/2017.
 */
public class BufferedEventMessage implements Serializable {
    List<Event> eventList;

    public BufferedEventMessage(List<Event> eventList) {
        this.eventList = new ArrayList<>(eventList);
    }

    public List<Event> getEventList() {
        return eventList;
    }
}
