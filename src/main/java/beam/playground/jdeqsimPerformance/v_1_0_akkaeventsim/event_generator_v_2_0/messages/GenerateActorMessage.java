package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.event_generator_v_2_0.messages;

import java.io.Serializable;

/**
 * Created by salma_000 on 7/5/2017.
 */
public class GenerateActorMessage implements Serializable {
    private long numberOfEvent;
    private String eventType;

    public GenerateActorMessage(int numberOfEvent, String eventType) {
        this.numberOfEvent = numberOfEvent;
        this.eventType = eventType;
    }

    public long getNumberOfEvent() {
        return numberOfEvent;
    }

    public String getEventType() {
        return eventType;
    }
}
