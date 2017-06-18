package beam.playground.jdeqsim.akkaeventsampling.Events;

import org.matsim.api.core.v01.events.GenericEvent;

public class PhySimTimeSyncEvent extends GenericEvent {
    private long startSyncTime;
    private long endSyncTime;

    public PhySimTimeSyncEvent(String type, double time, long startSyncTime, long endSyncTime) {
        super(type, time);
        this.startSyncTime = startSyncTime;
        this.endSyncTime = endSyncTime;
    }

    public long getStartSyncTime() {
        return startSyncTime;
    }

    public long getEndSyncTime() {
        return endSyncTime;
    }
}
