package beam.playground.jdeqsimPerformance.akkaeventsim;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by asif on 6/17/2017.
 */
public class RealTimeEventGenerator {

    int eventsCount = 0;
    int noOfEventsGenerated = 100;
    double timeRangeMin = 0;
    double timeRangeMax = 50000;

    int noOfVehicles = 100;
    int noOfLinks = 100;

    int noOfEventTypes = 2;
    public static final int LINK_ENTER_EVENT = 1;
    public static final int LINK_LEAVE_EVENT = 2;
    public static final int PHYSSIM_TIME_SYNC_EVENT = 3;

    Random random = new Random();

    public Event generateEvent() {

        double eventTime = getRandomEventTime();
        int eventType = getRandomEventType();
        Id<Vehicle> vehicleId = getRandomVehicleId();
        Id<Link> linkId = getRandomLinkId();

        if(eventsCount % 100 == 0){
            eventType = PHYSSIM_TIME_SYNC_EVENT;
        }

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

        eventsCount++;

        return event;
    }

    public double getRandomEventTime(){

        //return timeRangeMin + (timeRangeMax - timeRangeMin) * random.nextDouble();
        return timeRangeMin + random.nextDouble();
    }

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




    public static void main(String args[]){

        ActorSystem system = ActorSystem.create("akkaeventsim");
        ActorRef eventManagerActor = system.actorOf(Props.create(EventManagerActor.class), "EVENT_MANAGER");
        ActorRef eventBufferActor = system.actorOf(Props.create(EventBufferActor.class), "EVENT_BUFFER");



        RealTimeEventGenerator eventGenerator = new RealTimeEventGenerator();
        for(int i = 0; i < 1000; i++){

            Event event = eventGenerator.generateEvent();
            //System.out.println("Generated Event -> " + event);
            eventBufferActor.tell(event, ActorRef.noSender());
        }

    }
}
