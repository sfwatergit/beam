package beam.playground.jdeqsimPerformance.akkaeventsim.messages;

import java.io.Serializable;

/**
 * Created by salma_000 on 7/25/2017.
 */
public class GenerateEventMessage implements Serializable {
    int noOfEvents = 10000000;

    public GenerateEventMessage(int noOfEvents) {
        this.noOfEvents = noOfEvents;
    }

    public int getNoOfEvents() {
        return noOfEvents;
    }
}
