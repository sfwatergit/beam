package beam.playground.jdeqsimPerformance.akkaeventsim.events;

import org.matsim.api.core.v01.events.Event;

import java.io.Serializable;

/**
 * Created by asif on 6/17/2017.
 */
public class PhysSimTimeSyncEvent extends Event implements Serializable {

    String eventType = "PHYSSIM_TIME_SYNC_EVENT";

    public PhysSimTimeSyncEvent(double time) {
        super(time);
    }

    @Override
    public String getEventType() {
        return eventType;
    }
}
