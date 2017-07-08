package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.event_generator_v_2_0;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.PhysSimTimeSyncEvent;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.event_generator_v_2_0.messages.EventSimCompleteMessage;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.event_generator_v_2_0.messages.GenerateActorMessage;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.GenerateEventMessage;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.GenericEvent;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by salma_000 on 7/5/2017.
 */
public class EventGeneratorActor extends UntypedActor {
    public static final String ACTOR_NAME = "EventGeneratorAct";
    public static final int noOfVehicles = 100;
    public static final int noOfLinks = 1000;
    private static final Logger log = Logger.getLogger(EventGeneratorActor.class);
    public static long Gen_Start_Time;
    private ActorRef consumerActor;

    @Override
    public void preStart() throws Exception {
        this.consumerActor = getContext().actorOf(Props.create(EventConsumerActor.class), EventConsumerActor.ACTOR_NAME);
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof GenerateActorMessage) {
            GenerateActorMessage msg = (GenerateActorMessage) message;
            long startTime = System.currentTimeMillis();
            Gen_Start_Time = startTime;
            long endTime = System.currentTimeMillis();
            for (int i = 0; i < msg.getNumberOfEvent(); i++) {

                if (endTime - startTime >= 1000)
                    this.consumerActor.tell(this.createEvent(new GenerateEventMessage(GenerateEventMessage.PHY_SIM_TIME_SYNC_EVENT))
                            , ActorRef.noSender());


                this.consumerActor.tell(this.createEvent(new GenerateEventMessage(msg.getEventType()))
                        , ActorRef.noSender());

                endTime = System.currentTimeMillis();
            }
            this.consumerActor.tell(new EventSimCompleteMessage(msg.getNumberOfEvent()), ActorRef.noSender());

        }

    }

    private Event createEvent(GenerateEventMessage generateEventMessage) {
        Event event = null;
        int randomVehicleId = getRandomInt(1, noOfVehicles);
        Id<Vehicle> vehicleId = Id.createVehicleId("vehicle" + randomVehicleId);
        int randomLinkId = getRandomInt(1, noOfLinks);
        Id<Link> linkId = Id.createLinkId("link" + randomLinkId);

        double eventTime = System.currentTimeMillis();
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
                event = new PhysSimTimeSyncEvent(generateEventMessage.getEventType(), System.currentTimeMillis() % 1000, System.currentTimeMillis());
                break;
            default:
                throw new IllegalArgumentException("Invalid event type: " + generateEventMessage.getEventType());
        }
        if (generateEventMessage.getEventType() == null)
            log.debug("Create Event " + generateEventMessage.getEventType());

        return event;

    }

    public int getRandomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }


}
