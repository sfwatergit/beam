package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by salma_000 on 7/5/2017.
 */
public class EventManagerMessage implements Serializable {
    private List<?> eventList;
    private String eventType;

    public EventManagerMessage(List<?> eventList, String eventType) {
        this.eventList = new ArrayList<>(eventList);
        this.eventType = eventType;
    }

    public List<?> getEventList() {
        return eventList;
    }

    public String getEventType() {
        return eventType;
    }
}
