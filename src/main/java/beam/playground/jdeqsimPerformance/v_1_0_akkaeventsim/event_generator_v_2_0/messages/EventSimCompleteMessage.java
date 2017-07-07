package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.event_generator_v_2_0.messages;

import java.io.Serializable;

/**
 * Created by salma_000 on 7/7/2017.
 */
public class EventSimCompleteMessage implements Serializable {
    private long numberOfEvent = 0;

    public EventSimCompleteMessage(long numberOfEvent) {
        this.numberOfEvent = numberOfEvent;
    }

    public long getNumberOfEvent() {
        return numberOfEvent;
    }
}
