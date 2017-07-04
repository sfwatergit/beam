package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim;

import org.matsim.api.core.v01.events.Event;


public class PhysSimTimeSyncEvent extends Event {

    private final String type;
    private double timeThreshold;

    public PhysSimTimeSyncEvent(String type, double time, double timeThreshold) {

        super(time);
        this.type = type;
        this.timeThreshold = timeThreshold;
    }

    @Override
    public String getEventType() {
        return this.type;
    }

    public double getTimeThreshold() {
        return timeThreshold;
    }
}
