package beam.playground.jdeqsim.akkaeventsampling;

import akka.actor.ActorRef;
import beam.playground.jdeqsim.akkaeventsampling.messages.LoadBalancerMessageRequest;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.events.Event;
import org.matsim.core.events.EventsManagerImpl;

public class CustomEventManager extends EventsManagerImpl {

    private static final Logger log = Logger.getLogger(CustomEventManager.class);
    private final ActorRef eventLoadBalancer;

    public CustomEventManager(ActorRef eventRouter) {
        this.eventLoadBalancer = eventRouter;
    }

    @Override
    public void processEvent(final Event event) {
        this.eventLoadBalancer.tell(new LoadBalancerMessageRequest(event), ActorRef.noSender());
        super.processEvent(event);
    }

}
