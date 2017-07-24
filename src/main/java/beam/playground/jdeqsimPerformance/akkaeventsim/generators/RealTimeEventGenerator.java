package beam.playground.jdeqsimPerformance.akkaeventsim.generators;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.PhysSimTimeSyncEvent;
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
public class RealTimeEventGenerator extends UntypedActor{
    private static final int noOfVehicles = 100;
    private static final int noOfLinks = 100;
    private static final int noOfEventTypes = 2;
    private static final double timeRangeMax = 86400;
    private double timeRangeMin = 1;
    private double maxEventTimeReached = 0;
    private PerformanceParameter performanceParameter = new PerformanceParameter();
    private long generateNoOfEvents = 10000000;
    private ActorRef eventBufferActor = null;
    public RealTimeEventGenerator(ActorRef eventBufferActor) {

        this.eventBufferActor = eventBufferActor;
    }

    private Event generatePhysSimTimeSyncEvent() {

        double eventTime = (double)System.currentTimeMillis();
        Event event = new PhysSimTimeSyncEvent(eventTime);
        timeRangeMin = eventTime;
        return event;
    }

    private Event generateEvent() {

        double eventTime = (double)System.currentTimeMillis();
        int eventTypeId = getRandomEventType();
        Id<Vehicle> vehicleId = getRandomVehicleId();
        Id<Link> linkId = getRandomLinkId();
        EventType eventType = EventType.values()[eventTypeId];
        Event event = generateEvent(eventTime, eventType, vehicleId, linkId);
        return event;
    }

    private Event generateEvent(double eventTime, EventType eventType, Id<Vehicle> vehicleId, Id<Link> linkId) {
        Event event = null;
        switch (eventType){
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
            default:{
                event = new LinkEnterEvent(eventTime, vehicleId, linkId);
                break;
            }
        }

        if(event.getTime() > maxEventTimeReached){
            maxEventTimeReached = event.getTime();
        }

        return event;
    }

    private double getRandomEventTime() {
        return ThreadLocalRandom.current().nextDouble(timeRangeMin, timeRangeMax);
    }

    private int getRandomEventType() {
        return getRandomInt(1, noOfEventTypes);
    }

    private Id<Link> getRandomLinkId() {
        int id = getRandomInt(1, noOfLinks);
        return Id.createLinkId("link" + id);
    }

    private Id<Vehicle> getRandomVehicleId() {
        int id = getRandomInt(1, noOfVehicles);
        return Id.createVehicleId("vehicle" + id);
    }

    private int getRandomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private void generateEvents(){
        long startTime = System.currentTimeMillis();
        performanceParameter.setStartTime(startTime);

        for (int i = 0; i < generateNoOfEvents; i++) {

            long currentTime = System.currentTimeMillis();
            if(currentTime - startTime > 1000){
                Event event = generatePhysSimTimeSyncEvent();
                eventBufferActor.tell(event, ActorRef.noSender());
                startTime = currentTime;
            }else{
                Event event = generateEvent();
                eventBufferActor.tell(event, ActorRef.noSender());
            }
        }
        performanceParameter.setEndTime(System.currentTimeMillis());
        this.performanceParameter.setNoOfEvents(generateNoOfEvents);
        this.performanceParameter.calculateRateOfEventsReceived(getSelf().path().toString());
    }

    @Override
    public void onReceive(Object message) throws Throwable {


        if(message instanceof String){

            String msg = (String)message;
            if(msg.equalsIgnoreCase("GENERATE_EVENTS")){
                eventBufferActor.tell("START", getSelf());
                generateEvents();
                eventBufferActor.tell("END", getSelf());
                getSelf().tell("SHOW_COUNT", getSelf());
            }
        }
    }

    private enum EventType {
        PHYSSIM_TIME_SYNC_EVENT,
        LINK_LEAVE_EVENT,
        LINK_ENTER_EVENT
    }
}
