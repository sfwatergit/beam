package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.event_generator_v_2_0;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.EventsBufferActor;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.event_generator_v_2_0.messages.EventSimCompleteMessage;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.BufferEventMessage;
import org.matsim.api.core.v01.events.Event;

/**
 * Created by salma_000 on 7/5/2017.
 */
public class EventConsumerActor extends UntypedActor {
    public static final String ACTOR_NAME = "EventConsumerActor";
    public ActorRef bufferActorRef;

    @Override
    public void preStart() throws Exception {
        this.bufferActorRef = getContext().actorOf(Props.create(EventsBufferActor.class), EventsBufferActor.ACTOR_NAME);
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof Event) {
            Event msg = (Event) message;
            this.bufferActorRef.tell(new BufferEventMessage(msg, msg.getEventType()), ActorRef.noSender());
        } else if (message instanceof EventSimCompleteMessage) {
            EventSimCompleteMessage msg = (EventSimCompleteMessage) message;
            long duration = System.currentTimeMillis() - EventGeneratorActor.Gen_Start_Time;
            System.out.println("Total Time to generate event in sec " + (duration / 1000));
            System.out.println("Event generated per second" + (msg.getNumberOfEvent() / (duration / 1000)));

        }
    }
}
