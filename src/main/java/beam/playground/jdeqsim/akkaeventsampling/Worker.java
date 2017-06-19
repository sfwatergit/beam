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
    public static int linkEnterEventCount = 0;
    public static int linkLeaveEventCount = 0;
    public static int genericEventCount = 0;
    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof WorkerMessageRequest) {
            WorkerMessageRequest msg = (WorkerMessageRequest) message;
            if (msg.getRouterMessage().getEvent() instanceof LinkEnterEvent) {
                Dictionary.linkEnterEventList.add(msg.getRouterMessage().getEvent());
                linkEnterEventCount++;
            } else if (msg.getRouterMessage().getEvent() instanceof LinkLeaveEvent) {
                Dictionary.linkLeaveEventList.add(msg.getRouterMessage().getEvent());
                linkLeaveEventCount++;
            } else if (msg.getRouterMessage().getEvent() instanceof GenericEvent) {
                Dictionary.genericEventList.add(msg.getRouterMessage().getEvent());
                genericEventCount++;
            }

        }
    }
}
