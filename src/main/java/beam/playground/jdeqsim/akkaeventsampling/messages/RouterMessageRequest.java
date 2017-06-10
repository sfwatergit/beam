package beam.playground.jdeqsim.akkaeventsampling.messages;

import org.matsim.api.core.v01.events.Event;

import java.io.Serializable;


public class RouterMessageRequest implements IRequest, Serializable {
    Event event;

    public RouterMessageRequest(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
