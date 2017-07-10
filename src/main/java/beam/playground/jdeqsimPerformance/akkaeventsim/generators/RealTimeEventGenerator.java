package beam.playground.jdeqsimPerformance.akkaeventsim.generators;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.PhysSimTimeSyncEvent;
import beam.playground.jdeqsimPerformance.simpleeventsim.Util;
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
public class RealTimeEventGenerator extends UntypedActor{


    double timeRangeMax = 86400;
    int eventsCount = 0;
    double timeRangeMin = 1;
    int noOfVehicles = 100;
    int noOfLinks = 100;
    int noOfEventTypes = 2;
    double maxEventTimeReached = 0;

    public static final int LINK_ENTER_EVENT = 1;
    public static final int LINK_LEAVE_EVENT = 2;
    public static final int PHYSSIM_TIME_SYNC_EVENT = 3;

    long startTime = 0;
    long endTime = 0;
    long noOfEvents = 10000000;

    Random random = new Random();
    ActorRef eventBufferActor = null;

    RealTimeEventGenerator(ActorRef eventBufferActor){

        this.eventBufferActor = eventBufferActor;
    }

    RealTimeEventGenerator(ActorRef eventBufferActor, long noOfEvents){

        this.eventBufferActor = eventBufferActor;
        this.noOfEvents = noOfEvents;
    }

    public Event generatePhysSimTimeSyncEvent(){

        //double eventTime = getRandomEventTime();
        double eventTime = (double)System.currentTimeMillis();
        Event event = new PhysSimTimeSyncEvent(eventTime);
        timeRangeMin = eventTime;
        return event;
    }

    public Event generateEvent() {

        double eventTime = (double)System.currentTimeMillis();
        int eventType = getRandomEventType();
        Id<Vehicle> vehicleId = getRandomVehicleId();
        Id<Link> linkId = getRandomLinkId();

        /*if(eventsCount % 100 == 0){
            eventType = PHYSSIM_TIME_SYNC_EVENT;
        }*/

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

    public double getRandomEventTime(){

        //return timeRangeMin + (timeRangeMax - timeRangeMin) * random.nextDouble();
        //return timeRangeMin + random.nextDouble();
        return ThreadLocalRandom.current().nextDouble(timeRangeMin, timeRangeMax);
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

    private void generateEvents(){
        long _startTime = System.currentTimeMillis();
        startTime = _startTime;

        for(int i = 0; i < noOfEvents; i++){

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
        endTime = System.currentTimeMillis();
        Util.calculateRateOfEventsReceived(getSelf().path().toString(), _startTime, endTime, noOfEvents);
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
}
