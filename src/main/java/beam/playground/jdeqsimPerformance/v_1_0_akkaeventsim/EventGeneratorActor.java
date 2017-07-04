package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.BufferEventMessage;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.GenerateEventMessage;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.GenericEvent;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by salma_000 on 7/4/2017.
 */
public class EventGeneratorActor extends UntypedActor {
    public static final int noOfVehicles = 100;
    public static final int noOfLinks = 1000;
    //LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private static final Logger log = Logger.getLogger(EventGeneratorActor.class);
    private static double eventMinTimeRange = 1;
    private static double eventMaxTimeRange = 100;
    private ActorRef bufferActor;
    private int binSize = 100;
    private int PhySimEventCounter = 0;
    private Random random = new Random();

    public EventGeneratorActor(ActorRef bufferActor) {
        this.bufferActor = bufferActor;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof GenerateEventMessage) {
            GenerateEventMessage msg = (GenerateEventMessage) message;

            this.bufferActor.tell(createEvent(msg), ActorRef.noSender());
        } else {
            unhandled(message);
        }
    }

    private BufferEventMessage createEvent(GenerateEventMessage generateEventMessage) {
        Event event = null;
        int randomVehicleId = getRandomInt(1, noOfVehicles);
        Id<Vehicle> vehicleId = Id.createVehicleId("vehicle" + randomVehicleId);
        int randomLinkId = getRandomInt(1, noOfLinks);
        Id<Link> linkId = Id.createLinkId("link" + randomLinkId);

        double eventTime = eventMinTimeRange + (eventMaxTimeRange - eventMinTimeRange) * random.nextDouble();
        switch (generateEventMessage.getEventType()) {
            case GenerateEventMessage.LINK_ENTER_EVENT:
                event = new LinkEnterEvent(eventTime, vehicleId, linkId);
                break;
            case GenerateEventMessage.LINK_LEAVE_EVENT:
                event = new LinkLeaveEvent(eventTime, vehicleId, linkId);
                break;
            case GenerateEventMessage.GENERIC_EVENT:
                event = new GenericEvent(generateEventMessage.getEventType(), eventTime);
                break;
            case GenerateEventMessage.PHY_SIM_TIME_SYNC_EVENT:
                event = new PhysSimTimeSyncEvent(generateEventMessage.getEventType(), System.currentTimeMillis() % 1000, eventMaxTimeRange);
                PhySimEventCounter++;
                setEventTimeRange();
                break;
            default:
                throw new IllegalArgumentException("Invalid event type: " + generateEventMessage.getEventType());
        }
        if (generateEventMessage.getEventType() == null)
            log.debug("Create Event " + generateEventMessage.getEventType());

        return new BufferEventMessage(event);

    }

    public int getRandomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public void setEventTimeRange() {
        if (this.PhySimEventCounter > 0) {
            EventGeneratorActor.eventMinTimeRange = binSize * (this.PhySimEventCounter);
        }
        EventGeneratorActor.eventMaxTimeRange = binSize * (this.PhySimEventCounter + 1);
        log.debug("Bin Range " + EventGeneratorActor.eventMinTimeRange + "---" + EventGeneratorActor.eventMaxTimeRange);
    }


}
