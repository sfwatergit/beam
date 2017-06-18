package beam.playground.jdeqsim.akkaeventsampling.messages;

import java.io.Serializable;


public class SchedulerActorMessage implements IRequest, Serializable {

    public static final String LINK_ENTER_EVENT = "LinkEnterEvent";
    public static final String GENERIC_EVENT = "GenericEvent";
    public static final String LINK_LEAVE_EVENT = "LinkLeaveEvent";
    public static final String PHY_SIM_TIME_SYNC_EVENT = "PhySimTimeSyncEvent";

    private String eventType;
    private long syncStartTime;
    private long syncEndTime;

    public SchedulerActorMessage(String eventType) {
        this.eventType = eventType;
    }

    public SchedulerActorMessage(String eventType, long syncStartTime, long syncEndTime) {
        this.eventType = eventType;
        this.syncStartTime = syncStartTime;
        this.syncEndTime = syncEndTime;
    }

    public long getSyncStartTime() {
        return syncStartTime;
    }

    public long getSyncEndTime() {
        return syncEndTime;
    }

    public String getEventType() {
        return eventType;
    }
}
