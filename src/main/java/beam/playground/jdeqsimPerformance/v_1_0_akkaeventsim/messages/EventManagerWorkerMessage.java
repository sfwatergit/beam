package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages;


import java.io.Serializable;

/**
 * Created by salma_000 on 7/5/2017.
 */
public class EventManagerWorkerMessage implements Serializable {
    private EventManagerMessage eventManagerMessage;

    public EventManagerWorkerMessage(EventManagerMessage eventManagerMessage) {
        this.eventManagerMessage = eventManagerMessage;
    }

    public EventManagerMessage getEventManagerMessage() {
        return eventManagerMessage;
    }
}
