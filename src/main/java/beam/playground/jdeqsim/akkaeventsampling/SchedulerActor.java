package beam.playground.jdeqsim.akkaeventsampling;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import beam.playground.jdeqsim.akkaeventsampling.messages.LoadBalancerMessageRequest;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorMessage;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.GenericEvent;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;

import java.util.Random;


public class SchedulerActor extends UntypedActor {
    private static final Logger log = Logger.getLogger(SchedulerActor.class);
    private ActorRef eventLoadbalancer;
    private int[] randomNumberPool;

    public SchedulerActor(ActorRef eventLoadbalancer) {
        this.eventLoadbalancer = eventLoadbalancer;
        randomNumberPool = new Random().ints(100, 5, 120).toArray();
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof SchedulerActorMessage) {
            SchedulerActorMessage msg = (SchedulerActorMessage) message;
            if (msg.getMessageType().equalsIgnoreCase(SchedulerActorMessage.GENERATE_EVENT)) {
                this.eventLoadbalancer.tell(createRouterMessageRequest(), ActorRef.noSender());
            } else if (msg.getMessageType().equalsIgnoreCase(SchedulerActorMessage.SPECIAL_EVENT)) {
                this.eventLoadbalancer.tell(new LoadBalancerMessageRequest(new GenericEvent(msg.getMessageType(), /*LocalDateTime.now().getNano()*/System.currentTimeMillis() % 1000)), ActorRef.noSender());

            } else {
                this.eventLoadbalancer.tell(msg, ActorRef.noSender());
            }
        } else {
            unhandled(message);
        }
    }

    private LoadBalancerMessageRequest createRouterMessageRequest() {
        Long _eventTime = System.currentTimeMillis() % 1000;
        Double eventTime = _eventTime.doubleValue();
        int index = new Random().nextInt(randomNumberPool.length);

        Id<Vehicle> vehicleId = Id.createVehicleId("vehicle" + randomNumberPool[index]);
        //log.debug("Generate vehicleId"+"vehicle"+ randomNumberPool[index]);
        index = new Random().nextInt(randomNumberPool.length);
        Id<Link> linkId = Id.createLinkId("link" + randomNumberPool[index]);
        //log.debug("Generate linkId"+"link"+ randomNumberPool[index]);

        LinkEnterEvent linkEnterEvent = new LinkEnterEvent(eventTime, vehicleId, linkId);
        LoadBalancerMessageRequest loadBlanceMessageRequest = new LoadBalancerMessageRequest(linkEnterEvent);
        return loadBlanceMessageRequest;
    }
}
