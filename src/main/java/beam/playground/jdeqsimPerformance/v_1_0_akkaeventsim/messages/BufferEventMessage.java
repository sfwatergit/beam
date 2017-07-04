package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages;

import org.matsim.api.core.v01.events.Event;

import java.io.Serializable;

/**
 * Created by salma_000 on 7/4/2017.
 */
public class BufferEventMessage implements Serializable {
    private Event event;

    public BufferEventMessage(Event event) {
        this.event = event;
    }


    public Event getEvent() {
        return event;
    }
}
