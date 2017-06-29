package beam.playground.exceptions;

/**
 * Created by asif on 6/29/2017.
 */
public class InvalidEventTime extends Exception{

    @Override
    public String getMessage() {
        return "The timestamp for the event is smaller than the last PhysSimTimeSyncEvent timestamp";
    }
}
