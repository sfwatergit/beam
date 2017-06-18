package beam.playground.jdeqsim.akkaeventsampling;

import akka.actor.UntypedActor;
import beam.playground.jdeqsim.akkaeventsampling.messages.WorkerMessageRequest;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.events.GenericEvent;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;

public class Worker extends UntypedActor {
    public static final String ACTOR_NAME = "Worker";
    private static final Logger log = Logger.getLogger(Worker.class);

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof WorkerMessageRequest) {
            WorkerMessageRequest msg = (WorkerMessageRequest) message;
            if (msg.getRouterMessage().getEvent() instanceof LinkEnterEvent) {
                Dictionary.linkEnterEventList.add(msg.getRouterMessage().getEvent());
            } else if (msg.getRouterMessage().getEvent() instanceof LinkLeaveEvent) {
                Dictionary.linkLeaveEventList.add(msg.getRouterMessage().getEvent());
            } else if (msg.getRouterMessage().getEvent() instanceof GenericEvent) {
                Dictionary.genericEventList.add(msg.getRouterMessage().getEvent());
            }

        }
    }
}
