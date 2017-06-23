package beam.playground.jdeqsimPerformance.akkaeventsim;

import org.matsim.api.core.v01.events.Event;

/**
 * Created by asif on 6/17/2017.
 */
public class PhysSimTimeSyncEvent extends Event{

    String eventType = "PHYSSIM_TIME_SYNC_EVENT";

    public PhysSimTimeSyncEvent(double time) {
        super(time);
    }

    @Override
    public String getEventType() {
        return eventType;
    }
}
