package beam.playground.jdeqsim.akkaeventsampling.messages;

import org.matsim.api.core.v01.events.Event;

import java.io.Serializable;


public class LoadBalancerMessageRequest implements IRequest, Serializable {
    Event event;

    public LoadBalancerMessageRequest(Event event) {
        this.event = event;
    }


    public Event getEvent() {
        return event;
    }
}
