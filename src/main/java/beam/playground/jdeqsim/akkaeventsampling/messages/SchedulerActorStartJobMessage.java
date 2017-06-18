package beam.playground.jdeqsim.akkaeventsampling.messages;

import java.io.Serializable;


public class SchedulerActorStartJobMessage implements IRequest, Serializable {

    private static int identifier = 0;
    private long timeInMilliSec = 5;
    private boolean oneTimeJob = false;
    private String eventType;
    private int id;
    private long syncStartTime = -1;
    private long binSize = -1;

    public SchedulerActorStartJobMessage(long timeInMilliSec, String eventType) {
        this.timeInMilliSec = timeInMilliSec;
        this.eventType = eventType;
        id = identifier++;

    }

    public SchedulerActorStartJobMessage(long timeInMilliSec, String eventType, long syncStartTime, long binSize) {
        this.timeInMilliSec = timeInMilliSec;
        this.eventType = eventType;
        this.syncStartTime = syncStartTime;
        this.binSize = binSize;
        id = identifier++;

    }

    public SchedulerActorStartJobMessage(long timeInMilliSec, boolean oneTimeJob) {
        this.timeInMilliSec = timeInMilliSec;
        this.oneTimeJob = oneTimeJob;
        id = identifier++;
    }

    public long getSyncStartTime() {
        return syncStartTime;
    }

    public long getBinSize() {
        return binSize;
    }

    public String getEventType() {
        return eventType;
    }

    public int getId() {
        return id;
    }

    public long getTimeInMilliSec() {
        return timeInMilliSec;
    }

    public boolean isOneTimeJob() {
        return oneTimeJob;
    }
}
