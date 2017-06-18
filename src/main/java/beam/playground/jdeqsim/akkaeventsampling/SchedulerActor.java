package beam.playground.jdeqsim.akkaeventsampling;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import beam.playground.jdeqsim.akkaeventsampling.Events.PhySimTimeSyncEvent;
import beam.playground.jdeqsim.akkaeventsampling.messages.LoadBalancerMessageRequest;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorMessage;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.GenericEvent;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;

import java.util.Random;


public class SchedulerActor extends UntypedActor {
    private static final Logger log = Logger.getLogger(SchedulerActor.class);
    private ActorRef eventLoadbalancer;
    private int[] randomNumberPool;
    private double rangeMin = 1;
    private double eventTimeRangeMax = 1000;
    private Random random = new Random();
    private int PhySimEventCounter = 0;
    public SchedulerActor(ActorRef eventLoadbalancer) {
        this.eventLoadbalancer = eventLoadbalancer;
        randomNumberPool = new Random().ints(100, 5, 120).toArray();
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof SchedulerActorMessage) {
            SchedulerActorMessage msg = (SchedulerActorMessage) message;
            if (msg.getEventType().equalsIgnoreCase(SchedulerActorMessage.LINK_ENTER_EVENT)) {
                this.eventLoadbalancer.tell(createLinkEnterEvent(), ActorRef.noSender());
            } else if (msg.getEventType().equalsIgnoreCase(SchedulerActorMessage.LINK_LEAVE_EVENT)) {
                this.eventLoadbalancer.tell(createLinkLeaveEvent(), ActorRef.noSender());
            } else if (msg.getEventType().equalsIgnoreCase(SchedulerActorMessage.GENERIC_EVENT)) {
                this.eventLoadbalancer.tell(createGenericEvent(msg.getEventType()), ActorRef.noSender());
            } else if (msg.getEventType().equalsIgnoreCase(SchedulerActorMessage.PHY_SIM_TIME_SYNC_EVENT)) {
                PhySimEventCounter++;
                this.eventLoadbalancer.tell(createPhySimTimeSyncEvent(msg.getEventType(), msg.getSyncStartTime(), msg.getSyncEndTime()), ActorRef.noSender());

            } else {
                this.eventLoadbalancer.tell(msg, ActorRef.noSender());
            }
        } else {
            unhandled(message);
        }
    }

    private LoadBalancerMessageRequest createLinkEnterEvent() {

        double eventTime = rangeMin + (eventTimeRangeMax - rangeMin) * random.nextDouble();

        int index = new Random().nextInt(randomNumberPool.length);

        Id<Vehicle> vehicleId = Id.createVehicleId("vehicle" + randomNumberPool[index]);
        index = new Random().nextInt(randomNumberPool.length);
        Id<Link> linkId = Id.createLinkId("link" + randomNumberPool[index]);

        LinkEnterEvent linkEnterEvent = new LinkEnterEvent(eventTime, vehicleId, linkId);
        return new LoadBalancerMessageRequest(linkEnterEvent);
    }

    private LoadBalancerMessageRequest createLinkLeaveEvent() {
        double eventTime = rangeMin + (eventTimeRangeMax - rangeMin) * random.nextDouble();
        int index = new Random().nextInt(randomNumberPool.length);

        Id<Vehicle> vehicleId = Id.createVehicleId("vehicle" + randomNumberPool[index]);
        index = new Random().nextInt(randomNumberPool.length);
        Id<Link> linkId = Id.createLinkId("link" + randomNumberPool[index]);

        LinkLeaveEvent linkEnterEvent = new LinkLeaveEvent(eventTime, vehicleId, linkId);
        LoadBalancerMessageRequest loadBalanceMessageRequest = new LoadBalancerMessageRequest(linkEnterEvent);
        return loadBalanceMessageRequest;
    }

    private LoadBalancerMessageRequest createGenericEvent(String eventType) {
        double eventTime = rangeMin + (eventTimeRangeMax - rangeMin) * random.nextDouble();

        return new LoadBalancerMessageRequest(new GenericEvent(eventType, eventTime));
    }

    private LoadBalancerMessageRequest createPhySimTimeSyncEvent(String eventType, long startSyncTime, long binSize) {

        if (this.PhySimEventCounter > 1) {
            startSyncTime = binSize * (this.PhySimEventCounter - 1);
        }
        long endSyncTime = binSize * this.PhySimEventCounter;
        return new LoadBalancerMessageRequest(new PhySimTimeSyncEvent(eventType, System.currentTimeMillis() % 1000, startSyncTime, endSyncTime));
    }
}
