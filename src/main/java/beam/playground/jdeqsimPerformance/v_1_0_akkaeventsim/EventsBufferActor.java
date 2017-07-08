package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import beam.playground.jdeqsim.akkaeventsampling.EventTimeComparator;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.eventprocessor.EventProcessActor;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.BufferEventMessage;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.GenerateEventMessage;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.ProcessActorMessage;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.events.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by salma_000 on 7/3/2017.
 */
public class EventsBufferActor extends UntypedActor {
    public static final String ACTOR_NAME = "EventsBufferActor";
    private static final Logger log = Logger.getLogger(EventsBufferActor.class);
    //LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    public static long receivedEventCount = 0;
    Queue<Event> eventQueue = new PriorityQueue<>(10000, new EventTimeComparator());
    private ActorRef processActor;

    public void preStart() throws Exception {
        this.processActor = getContext().actorOf(Props.create(EventProcessActor.class), EventProcessActor.ACTOR_NAME);
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof BufferEventMessage) {
            BufferEventMessage msg = (BufferEventMessage) message;
            //log.debug("Received Message" + msg.getEvent().toString());
            if (!msg.getEventType().equalsIgnoreCase(GenerateEventMessage.PHY_SIM_TIME_SYNC_EVENT)) {
                if (msg.getEventList() != null) {
                    for (Event event : msg.getEventList()) {
                        receivedEventCount++;
                        this.eventQueue.add(event);
                    }
                }
                if (msg.getEvent() != null) {
                    receivedEventCount++;
                    this.eventQueue.add(msg.getEvent());
                }
                if (receivedEventCount == 10000000) {
                    receivedEventCount = 0;
                    log.debug("Duration to generate 10000000 in sec =" + ((System.currentTimeMillis() - EventSimMain.startTime) / 1000));
                }

            } else {
                PhysSimTimeSyncEvent phyTimEvent = null;
                if (msg.getEventList() != null)
                    phyTimEvent = (PhysSimTimeSyncEvent) msg.getEventList().get(0);
                else
                    phyTimEvent = (PhysSimTimeSyncEvent) msg.getEvent();
                this.processActor.tell(new ProcessActorMessage(getEvents(phyTimEvent.getTimeThreshold())), ActorRef.noSender());
            }

        }

    }

    public List<Event> getEvents(double timeThreshold) {
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

}
