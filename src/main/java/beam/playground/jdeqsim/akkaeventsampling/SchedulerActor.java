package beam.playground.jdeqsim.akkaeventsampling;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import beam.playground.jdeqsim.akkaeventsampling.messages.RouterMessageRequest;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorMessage;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.events.GenericEvent;


public class SchedulerActor extends UntypedActor {
    private static final Logger log = Logger.getLogger(SchedulerActor.class);
    private ActorRef eventRouter;

    public SchedulerActor(ActorRef eventRouter) {
        this.eventRouter = eventRouter;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof SchedulerActorMessage) {
            SchedulerActorMessage msg = (SchedulerActorMessage) message;
            if (msg.getMessageType().equalsIgnoreCase(SchedulerActorMessage.GENERATE_EVENT) || msg.getMessageType().equalsIgnoreCase(SchedulerActorMessage.SPECIAL_EVENT)) {
                this.eventRouter.tell(new RouterMessageRequest(new GenericEvent(msg.getMessageType(), /*LocalDateTime.now().getNano()*/System.currentTimeMillis() % 1000)), ActorRef.noSender());
            } else {
                this.eventRouter.tell(msg, ActorRef.noSender());
            }
        } else {
            unhandled(message);
        }
    }
}
