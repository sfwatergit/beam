package beam.playground.jdeqsimPerformance;

import beam.playground.jdeqsimPerformance.akkaeventsim.events.PhysSimTimeSyncEvent;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by asif on 7/7/2017.
 */
public class EventGenerator {

    public static final int LINK_ENTER_EVENT = 1;
    public static final int LINK_LEAVE_EVENT = 2;
    public static final int PHYSSIM_TIME_SYNC_EVENT = 3;

    int noOfEventsGenerated = 100;
    double timeRangeMax = 86400;
    int eventsCount = 0;
    double timeRangeMin = 1;
    int noOfVehicles = 100;
    int noOfLinks = 100;
    int noOfEventTypes = 2;
    double maxEventTimeReached = 0;


    Random random = new Random();

    public Event generatePhysSimTimeSyncEvent(){

        //double eventTime = getRandomEventTime();
        double eventTime = (double)System.currentTimeMillis();
        Event event = new PhysSimTimeSyncEvent(eventTime);
        timeRangeMin = eventTime;
        return event;
    }

    public Event generateEvent() {

        //double eventTime = getRandomEventTime();
        double eventTime = (double)System.currentTimeMillis();
        int eventType = getRandomEventType();
        Id<Vehicle> vehicleId = getRandomVehicleId();
        Id<Link> linkId = getRandomLinkId();

        Event event = generateEvent(eventTime, eventType, vehicleId, linkId);
        return event;
    }

    public Event generateEvent(double eventTime, int eventType, Id<Vehicle> vehicleId, Id<Link> linkId){
        Event event = null;

        //System.out.println("Generating event of event type " + eventType);
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

        eventsCount++;
        return event;
    }

    public double getRandomEventTime(){ return ThreadLocalRandom.current().nextDouble(timeRangeMin, timeRangeMax); }
    public int getRandomEventType(){
        return getRandomInt(1, noOfEventTypes);
    }

    public Id<Link> getRandomLinkId(){
        int id = getRandomInt(1, noOfLinks);
        return Id.createLinkId("link" + id);
    }

    public Id<Vehicle> getRandomVehicleId(){
        int id = getRandomInt(1, noOfVehicles);
        return Id.createVehicleId("vehicle" + id);
    }

    public int getRandomInt(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

}
