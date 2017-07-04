package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages;

import java.io.Serializable;

/**
 * Created by salma_000 on 7/4/2017.
 */
public class GenerateEventMessage implements Serializable {
    public static final String LINK_ENTER_EVENT = "LinkEnterEvent";
    public static final String GENERIC_EVENT = "GenericEvent";
    public static final String LINK_LEAVE_EVENT = "LinkLeaveEvent";
    public static final String PHY_SIM_TIME_SYNC_EVENT = "PhySimTimeSyncEvent";
    private String eventType;

    public GenerateEventMessage(String eventType) {
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }
}
