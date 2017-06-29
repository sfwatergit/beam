package beam.playground.exceptions;

/**
 * Created by asif on 6/29/2017.
 */
public class InvalidEventTime extends Exception{


    public InvalidEventTime(String message) {
        super(message);
    }

    public InvalidEventTime(Throwable cause) {
        super(cause);
    }

    public InvalidEventTime(String message, Throwable cause) {
        super(message, cause);
    }
}
