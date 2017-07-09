package beam.playground.jdeqsimPerformance.akkaeventsim;

import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.PhysSimTimeSyncEvent;
import beam.playground.jdeqsimPerformance.akkaeventsim.util.EventTimeComparator;
import beam.playground.jdeqsimPerformance.simpleeventsim.SimulationTimes;
import beam.playground.jdeqsimPerformance.simpleeventsim.Util;
import org.matsim.api.core.v01.events.Event;

import java.util.*;

/**
 * Created by asif on 6/25/2017.
 */
public class EventBufferActor extends UntypedActor {


    int startMessageCount = 0;
    int endMessageCount = 0;
    long noOfEventsReceived = 0;
    long lastEventReceiptTime = 0;
    long firstEventReceivedTime = 0;
    boolean simulationCompletedFlag = false;

    PhysSimTimeSyncEvent physSimTimeSyncEvent = null;
    Event eventReceived = null;

    Queue<Event> eventQueue = new PriorityQueue<>(100, new EventTimeComparator());
    List<Event> eventList = new ArrayList<>();
    List<Event> eventLinkedList = new LinkedList<>();
    //Queue<Event> eventQueue = new PriorityQueue<>(100, new EventTimeComparator());

    public EventBufferActor(){
    }

    public List<Event> getEvents(double timeThreshold){
        List<Event> events = new ArrayList<>();
        while(eventQueue.size() > 0){

            Event event = eventQueue.poll();
            if(timeThreshold > event.getTime()) {
                events.add(event);
            }else{
                break;
            }
        }
        return events;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof PhysSimTimeSyncEvent) {

            handlePhysSimTimeSyncEvent(message);
        }else if(message instanceof Event){

            handleEvent(message);
        }else if(message instanceof List){

            handleEventList(message);
        }else if(message instanceof SimulationTimes){

            handleSimulationTimesMessage(message);

        }else if(message instanceof String){

            handleMessage(message);
        }
    }

    public void handleSimulationTimesMessage(Object message){
        simulationCompletedFlag = true;
        SimulationTimes simulationTimes = (SimulationTimes)message;

        System.out.println(getSelf().toString() + ", First Event Received at " + firstEventReceivedTime +
                ", Last Event Recived at " + lastEventReceiptTime +
                ", No Of Events " + noOfEventsReceived);
        Util.calculateRateOfEventsReceived(getSelf().path().toString(), firstEventReceivedTime, lastEventReceiptTime, noOfEventsReceived);

    }

    public void handlePhysSimTimeSyncEvent(Object message){

        physSimTimeSyncEvent = (PhysSimTimeSyncEvent)message;
        List<Event> events = getEvents(physSimTimeSyncEvent.getTime());
        getContext().actorSelection("../EVENT_MANAGER").tell(events, getSelf());
    }

    public void handleEvent(Object message){

        /*
        Event event = (Event)message;

            if(physSimTimeSyncEvent != null && event.getTime() < physSimTimeSyncEvent.getTime()){
                throw new InvalidEventTime("The timestamp for the event is smaller than the last PhysSyncTimeEvent timestamp");
            }

            eventQueue.add(event);
         */

        if(simulationCompletedFlag == true) {
            System.out.println("Message received after simulationCompletedFlag is set " + simulationCompletedFlag);
        }
        if(noOfEventsReceived == 0){
            firstEventReceivedTime = System.currentTimeMillis();
            //System.out.println(getSelf().path().toString() + " -> First Event Received at " + firstEventReceivedTime);
        }
        eventReceived = (Event)message;
        eventQueue.add(eventReceived);
        //eventList.add(eventReceived);
        //eventLinkedList.add(eventReceived);
        noOfEventsReceived++;
        lastEventReceiptTime = System.currentTimeMillis();

        if(noOfEventsReceived == 10000000){
            //System.out.println(getSelf().path().toString() + " -> Last Event Received at " + lastEventReceiptTime);
        }
    }

    public void handleEventList(Object message){
        if(noOfEventsReceived == 0){
            firstEventReceivedTime = System.currentTimeMillis();
            //System.out.println(getSelf().path().toString() + " -> First Event Received at " + firstEventReceivedTime);
        }
        List<Event> eventsReceived = (List<Event>)message;
        noOfEventsReceived += eventsReceived.size();
        lastEventReceiptTime = System.currentTimeMillis();

        if(noOfEventsReceived == 10000000){
            //System.out.println(getSelf().path().toString() + " -> Last Event Received at " + lastEventReceiptTime + ", noOfEventsReceived: " + noOfEventsReceived);
        }
    }

    public void handleMessage(Object message){

        /*
        String _message = (String)message;
            if(_message.equals("SIM_COMPLETED")){
                System.out.println("Sim completed received in event buffer");
                getContext().actorSelection("../EVENT_MANAGER").tell("SIM_COMPLETED", getSelf());
            }
         */

        if(((String) message).equalsIgnoreCase("START")){
            startMessageCount++;

        }else if(((String) message).equalsIgnoreCase("END")){
            endMessageCount++;
            if(endMessageCount == startMessageCount){
                Util.calculateRateOfEventsReceived(getSelf().path().toString(), firstEventReceivedTime, lastEventReceiptTime, noOfEventsReceived);
            }
        }
    }
}
