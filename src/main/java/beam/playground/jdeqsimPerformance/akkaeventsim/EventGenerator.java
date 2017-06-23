package beam.playground.jdeqsimPerformance.akkaeventsim;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by asif on 6/17/2017.
 */
public class EventGenerator {


    int noOfEventsGenerated = 100;
    double rangeMin = 1;
    double eventTimeRangeMax = 1000;
    Random random = new Random();

    public static final int LINK_ENTER_EVENT = 1;
    public static final int LINK_LEAVE_EVENT = 2;
    int noOfEventTypes = 2;

    int noOfVehicles = 100;
    int noOfLinks = 100;

    List<Event> allGeneratedEvents = new ArrayList<>();

    public Event generateEvent(){

        int eventType = getRandomInt(1, noOfEventTypes);

        int randomVehicleId = getRandomInt(1, noOfVehicles);
        Id<Vehicle> vehicleId = Id.createVehicleId("vehicle" + randomVehicleId);

        int randomLinkId = getRandomInt(1, noOfLinks);
        Id<Link> linkId = Id.createLinkId("link" + randomLinkId);

        double eventTime = rangeMin + (eventTimeRangeMax - rangeMin) * random.nextDouble();

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
            default:{
                event = new LinkEnterEvent(eventTime, vehicleId, linkId);
                break;
            }
        }

        return event;
    }

    public int getRandomInt(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public void generateEvents(){

        for(int i = 0; i < noOfEventsGenerated; i++){
            allGeneratedEvents.add(generateEvent());
        }
    }

    public void printAllGeneratedEvents(){
        for(Event event : allGeneratedEvents){
            System.out.println("Event -> " + event.getAttributes());
        }
    }

    public void printEvents(List<Event> events){
        for(Event event : events){
            System.out.println("Event -> " + event.getAttributes());
        }
    }

    public void printEvents(PriorityQueue<Event> events){
        for(Event event : events){
            System.out.println("Event -> " + event.getAttributes());
        }
    }

    public void printEvents(Set<Event> events){
        for(Event event : events){
            System.out.println("Event -> " + event.getAttributes());
        }
    }

    public Set<Event> getEvents(double startTime, double endTime){

        List<Event> events = allGeneratedEvents.stream()
                .filter(event -> event.getTime() >= (startTime) && event.getTime() < endTime)
                .collect(Collectors.toList());

        Set eventSet = new TreeSet(new EventTimeComparator());

        /*PriorityQueue queue = new PriorityQueue(events.size(), new EventTimeComparator());
        for(Event event : events) {
            queue.add(event);
        }
        //queue.addAll(events);
        return queue;*/
        eventSet.addAll(events);
        return eventSet;
    }



    public static void main(String args[]){

        EventGenerator eventGenerator = new EventGenerator();
        eventGenerator.generateEvents();
        eventGenerator.printAllGeneratedEvents();

        int binSize = 100;
        int totalEventsFound = 0;
        for(int i = 0; i < eventGenerator.eventTimeRangeMax; i = i + binSize) {
            System.out.println("Get events with times between [" + (i) + "," + (i + binSize) + ")");
            Set<Event> events = eventGenerator.getEvents(i, i + binSize);
            System.out.println(events.size());
            eventGenerator.printEvents(events);
            System.out.println("--");
            totalEventsFound = totalEventsFound + events.size();


        }

        System.out.println("Total Events Generated " + eventGenerator.allGeneratedEvents.size());
        System.out.println("Total Events Found -> " + totalEventsFound);
    }
}
