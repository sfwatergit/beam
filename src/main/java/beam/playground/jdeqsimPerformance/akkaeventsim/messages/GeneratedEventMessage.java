package beam.playground.jdeqsimPerformance.akkaeventsim.messages;

import java.io.Serializable;

/**
 * Created by salma_000 on 8/2/2017.
 */
public class GeneratedEventMessage implements Serializable {
    private String strEvent;
    private byte[] eventByteArray;
    private String eventType;

    public GeneratedEventMessage(String event, String eventType) {
        //this.strEvent = event;
        eventByteArray = event.getBytes();
        this.eventType = eventType;
    }

    public String getStrEvent() {
        return strEvent;
    }

    public byte[] getByteEvent() {
        return eventByteArray;
    }

    public String getEventType() {
        return eventType;
    }
}
