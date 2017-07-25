package beam.playground.jdeqsimPerformance.akkaeventsim.generators;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.PhysSimTimeSyncEvent;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.EndSimulationMessage;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.GenerateEventMessage;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.StartSimulationMessage;
import beam.playground.jdeqsimPerformance.akkaeventsim.util.PerformanceParameter;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by asif on 6/17/2017.
 */
public class RealTimeEventGenerator extends UntypedActor {
    private static final long PHY_SYN_EVENT_TIME = 1000;
    private static final int NO_OF_VEHICLES = 100;
    private static final int NO_OF_LINKS = 100;
    private static final int NO_OF_EVENT_TYPES = 2;
    private static final double TIME_RANGE_MAX = 86400;
    private double timeRangeMin = 1;
    private double maxEventTimeReached = 0;
    private PerformanceParameter performanceParameter = new PerformanceParameter();
    private ActorRef eventBufferActor = null;
    public RealTimeEventGenerator(ActorRef eventBufferActor) {
        this.eventBufferActor = eventBufferActor;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof GenerateEventMessage) {
            GenerateEventMessage msg = (GenerateEventMessage) message;
            eventBufferActor.tell(new StartSimulationMessage(), getSelf());
            generateEvents(msg.getNoOfEvents());
            eventBufferActor.tell(new EndSimulationMessage(), getSelf());
        } else {
            unhandled(message);
        }
    }

    private Event generatePhysSimTimeSyncEvent() {
        double eventTime = (double) System.currentTimeMillis();
        Event event = new PhysSimTimeSyncEvent(eventTime);
        timeRangeMin = eventTime;
        return event;
    }

    private Event generateEvent() {
        double eventTime = (double) System.currentTimeMillis();
        int eventTypeId = getRandomEventType();
        Id<Vehicle> vehicleId = getRandomVehicleId();
        Id<Link> linkId = getRandomLinkId();
        EventType eventType = EventType.values()[eventTypeId];
        Event event = generateEvent(eventTime, eventType, vehicleId, linkId);
        return event;
    }

    private Event generateEvent(double eventTime, EventType eventType, Id<Vehicle> vehicleId, Id<Link> linkId) {
        Event event = null;
        switch (eventType) {
            case LINK_ENTER_EVENT: {
                event = new LinkEnterEvent(eventTime, vehicleId, linkId);
                break;
            }
            case LINK_LEAVE_EVENT: {
                event = new LinkLeaveEvent(eventTime, vehicleId, linkId);
                break;
            }
            case PHYSSIM_TIME_SYNC_EVENT: {
                event = new PhysSimTimeSyncEvent(eventTime);
                timeRangeMin = eventTime;
                break;
            }
            default: {
                event = new LinkEnterEvent(eventTime, vehicleId, linkId);
                break;
            }
        }
        if (event.getTime() > maxEventTimeReached) {
            maxEventTimeReached = event.getTime();
        }
        return event;
    }

    private double getRandomEventTime() {
        return ThreadLocalRandom.current().nextDouble(timeRangeMin, TIME_RANGE_MAX);
    }

    private int getRandomEventType() {
        return getRandomInt(1, NO_OF_EVENT_TYPES);
    }

    private Id<Link> getRandomLinkId() {
        int id = getRandomInt(1, NO_OF_LINKS);
        return Id.createLinkId("link" + id);
    }

    private Id<Vehicle> getRandomVehicleId() {
        int id = getRandomInt(1, NO_OF_VEHICLES);
        return Id.createVehicleId("vehicle" + id);
    }

    private int getRandomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private void generateEvents(int noOfEvents) {
        long startTime = System.currentTimeMillis();
        performanceParameter.setStartTime(startTime);
        for (int i = 0; i < noOfEvents; i++) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime > PHY_SYN_EVENT_TIME) {
                Event event = generatePhysSimTimeSyncEvent();
                eventBufferActor.tell(event, ActorRef.noSender());
                startTime = currentTime;
            } else {
                Event event = generateEvent();
                eventBufferActor.tell(event, ActorRef.noSender());
            }
        }
        performanceParameter.setEndTime(System.currentTimeMillis());
        this.performanceParameter.setNoOfEvents(noOfEvents);
        this.performanceParameter.calculateRateOfEventsReceived(getSelf().path().toString());
    }

    private enum EventType {
        PHYSSIM_TIME_SYNC_EVENT,
        LINK_LEAVE_EVENT,
        LINK_ENTER_EVENT
    }
}
