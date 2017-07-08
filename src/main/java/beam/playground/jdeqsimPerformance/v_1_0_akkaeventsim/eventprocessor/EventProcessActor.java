package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.eventprocessor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.EventManagerActor;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.EventManagerMessage;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.GenerateEventMessage;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.ProcessActorMessage;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;

import java.util.List;

import static java.util.stream.Collectors.toList;


/**
 * Created by salma_000 on 7/4/2017.
 */
public class EventProcessActor extends UntypedActor {
    public static final String ACTOR_NAME = "EventManagerActor";
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private ActorRef eventManagerActor;
    private ActorRef worker;

    public void preStart() throws Exception {
        this.eventManagerActor = getContext().actorOf(Props.create(EventManagerActor.class), EventManagerActor.ACTOR_NAME);

    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof ProcessActorMessage) {
            ProcessActorMessage msg = (ProcessActorMessage) message;
            List<LinkEnterEvent> linkEnterEventList = msg.getEventList().stream()
                    .filter(LinkEnterEvent.class::isInstance)
                    .map(LinkEnterEvent.class::cast)
                    .collect(toList());
            List<LinkLeaveEvent> linkLeaveEventList = msg.getEventList().stream()
                    .filter(LinkLeaveEvent.class::isInstance)
                    .map(LinkLeaveEvent.class::cast)
                    .collect(toList());

            this.eventManagerActor.tell(new EventManagerMessage(linkEnterEventList, GenerateEventMessage.LINK_ENTER_EVENT), ActorRef.noSender());
            this.eventManagerActor.tell(new EventManagerMessage(linkLeaveEventList, GenerateEventMessage.LINK_LEAVE_EVENT), ActorRef.noSender());
            this.eventManagerActor.tell(new EventManagerMessage(msg.getEventList(), GenerateEventMessage.ALL_EVENT), ActorRef.noSender());
        }
    }

}
