package beam.playground.jdeqsimPerformance.akkaeventsim;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import beam.playground.exceptions.InvalidEventTime;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.PhysSimTimeSyncEvent;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.BufferedEventMessage;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.EndSimulationMessage;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.StartSimulationMessage;
import beam.playground.jdeqsimPerformance.akkaeventsim.util.EventTimeComparator;
import beam.playground.jdeqsimPerformance.akkaeventsim.util.PerformanceParameter;
import org.matsim.api.core.v01.events.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by asif on 6/25/2017.
 */
public class EventBufferActor extends UntypedActor {

    //List<Event> eventList = new ArrayList<>();
    //List<Event> eventLinkedList = new LinkedList<>();
    //Queue<Event> eventQueue = new PriorityQueue<>(100, new EventTimeComparator());
    private static Queue<Event> eventQueue = new PriorityQueue<>(100, new EventTimeComparator());
    private ActorRef eventManagerActor = null;
    private int startMessageCount = 0;
    private int endMessageCount = 0;
    private PerformanceParameter performanceParameter = new PerformanceParameter();
    private int physSimTimeSyncEventCount = 0;
    private double lastPhysSimTimeSyncEventTime = -1;

    public EventBufferActor(ActorRef eventManagerActor) {
        this.eventManagerActor = eventManagerActor;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof PhysSimTimeSyncEvent) {
            handlePhysSimTimeSyncEvent(message);
        } else if (message instanceof Event) {
            handleEvent(message);
        }
        handleMessage(message);
    }

    private List<Event> getEvents(double timeThreshold) {
        List<Event> events = new ArrayList<>();
        while (eventQueue.size() > 0) {
            Event event = eventQueue.poll();
            if (timeThreshold > event.getTime()) {
                events.add(event);
            } else {
                break;
            }
        }
        return events;
    }

    private void handlePhysSimTimeSyncEvent(Object message) {
        physSimTimeSyncEventCount++;
        this.performanceParameter.updateStatistics(1);
        PhysSimTimeSyncEvent physSimTimeSyncEvent = (PhysSimTimeSyncEvent) message;
        lastPhysSimTimeSyncEventTime = physSimTimeSyncEvent.getTime();
        List<Event> events = getEvents(lastPhysSimTimeSyncEventTime);
        eventManagerActor.tell(new BufferedEventMessage(events), getSelf());
    }

    private void handleEvent(Object message) throws InvalidEventTime {
        this.performanceParameter.updateStatistics(1);
        Event eventReceived = (Event) message;
        if (lastPhysSimTimeSyncEventTime != -1 && eventReceived.getTime() < lastPhysSimTimeSyncEventTime) {
            throw new InvalidEventTime("The timestamp for the event is smaller than the last PhysSyncTimeEvent timestamp");
        }
        eventQueue.add(eventReceived);
        //eventList.add(eventReceived);
        //eventLinkedList.add(eventReceived);
    }

    private void handleMessage(Object message) {
        if (message instanceof StartSimulationMessage) {
            startMessageCount++;
        } else if (message instanceof EndSimulationMessage) {
            endMessageCount++;
            if (endMessageCount == startMessageCount) {
                System.out.println(getSelf() + " -> Remaining Queue size -> " + eventQueue.size() + ", No of PhysSimTimeSyncEvent Received -> " + physSimTimeSyncEventCount);
                if (!eventQueue.isEmpty()) {
                    List<Event> events = getEvents(System.currentTimeMillis());
                    eventManagerActor.tell(new BufferedEventMessage(events), getSelf());
                }
                this.performanceParameter.calculateRateOfEventsReceived(getSelf().path().toString());
                eventManagerActor.tell(new EndSimulationMessage(), getSelf());
            }
        } else {
            unhandled(message);
        }
    }
}
