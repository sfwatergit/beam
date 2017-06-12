package beam.playground.jdeqsim.akkaeventsampling;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import beam.playground.jdeqsim.akkaeventsampling.messages.RouterMessageRequest;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorMessage;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.GenericEvent;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;


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

    private RouterMessageRequest createRouterMessageRequest(){
        Long _eventTime = System.currentTimeMillis() % 1000;
        Double eventTime = _eventTime.doubleValue();

        Id<Vehicle> vehicleId = Id.create("v1", org.matsim.vehicles.Vehicle.class);
        Id<Link> linkId = Id.createLinkId("link1");


        LinkEnterEvent linkEnterEvent = new LinkEnterEvent(eventTime, vehicleId, linkId);
        RouterMessageRequest routerMessageRequest = new RouterMessageRequest(linkEnterEvent);
        return routerMessageRequest;
    }
}
