package beam.playground.jdeqsimPerformance.akkacluterawareeventsim.loadbalancer;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import beam.playground.exceptions.InvalidEventTime;
import beam.playground.jdeqsimPerformance.akkaeventsim.events.PhysSimTimeSyncEvent;
import beam.playground.jdeqsimPerformance.akkaeventsim.messages.*;
import beam.playground.jdeqsimPerformance.akkaeventsim.util.EventTimeComparator;
import beam.playground.jdeqsimPerformance.akkaeventsim.util.PerformanceParameter;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by salma_000 on 8/3/2017.
 */
public class EventBufferWorker extends UntypedActor {
    public static final String LINK_ENTER_EVENT = "entered link";
    public static final String LINK_LEAVE_EVENT = "left link";
    public static final String PHY_SIM_TIME_SYNC_EVENT = "PHYSSIM_TIME_SYNC_EVENT";
    private Queue<Event> eventQueue = new PriorityQueue<>(100, new EventTimeComparator());
    private ActorRef eventCollectorActor = null;
    private int startMessageCount = 0;
    private int endMessageCount = 0;
    private PerformanceParameter performanceParameter = new PerformanceParameter();
    private int physSimTimeSyncEventCount = 0;
    private double lastPhysSimTimeSyncEventTime = -1;
    private Unmarshaller u = null;

    public EventBufferWorker(ActorRef eventCollector) {
        this.eventCollectorActor = eventCollector;
        try {
            JAXBContext jc = JAXBContext.newInstance(EventSerializable.class);
            u = jc.createUnmarshaller();
        } catch (JAXBException oops) {

        }

    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof GeneratedEventMessage) {
            GeneratedEventMessage msg = (GeneratedEventMessage) message;
            //String strEvent = msg.getStrEvent();
            String strEvent = new String(msg.getByteEvent(), "UTF-8");
            Event event = unMarshalEvent(strEvent);
            if (event instanceof PhysSimTimeSyncEvent)
                handlePhysSimTimeSyncEvent(event);
            else
                handleEvent(event);
        }
        handleMessage(message);
    }

    private Event unMarshalEvent(String strEvent) throws JAXBException {
        Event event = null;
        try {

            Object o = this.u.unmarshal(new StreamSource(new StringReader(strEvent)));
            EventSerializable serEvent = (EventSerializable) o;


            Id<Link> link = null;
            Id<Vehicle> vehicle = null;
            if (serEvent.getLink() != null)
                link = Id.createLinkId(serEvent.getLink());
            if (serEvent.getVehicle() != null)
                vehicle = Id.createVehicleId(serEvent.getVehicle());
            switch (serEvent.getType()) {
                case EventBufferWorker.LINK_ENTER_EVENT:
                    event = new LinkEnterEvent(serEvent.getTime(), vehicle, link);
                    break;
                case EventBufferWorker.LINK_LEAVE_EVENT:
                    event = new LinkLeaveEvent(serEvent.getTime(), vehicle, link);
                    break;
                case EventBufferWorker.PHY_SIM_TIME_SYNC_EVENT:
                    event = new PhysSimTimeSyncEvent(serEvent.getTime());
                    break;
                default:
                    throw new IllegalArgumentException("Invalid event type: " + serEvent.getType());
            }
        } catch (Exception e) {
            System.out.println("Exception " + strEvent);
        }
        return event;
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
        this.eventCollectorActor.tell(new BufferedEventMessage(events), getSelf());
    }

    private void handleEvent(Object message) throws InvalidEventTime {
        this.performanceParameter.updateStatistics(1);
        Event eventReceived = (Event) message;
        //System.out.println("Received Event "+eventReceived.toString());
        if (lastPhysSimTimeSyncEventTime != -1 && eventReceived.getTime() < lastPhysSimTimeSyncEventTime) {
            throw new InvalidEventTime("The timestamp for the event is smaller than the last PhysSyncTimeEvent timestamp");
        }
        eventQueue.add(eventReceived);
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
                    this.eventCollectorActor.tell(new BufferedEventMessage(events), getSelf());
                }
                this.performanceParameter.calculateRateOfEventsReceived(getSelf().path().toString());
                this.eventCollectorActor.tell(new EndSimulationMessage(), getSelf());
            }
        } else {
            unhandled(message);
        }
    }

}
