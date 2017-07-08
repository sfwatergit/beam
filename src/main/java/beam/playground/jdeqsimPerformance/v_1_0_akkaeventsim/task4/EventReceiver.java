package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.task4;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.event_generator_v_2_0.messages.EventSimCompleteMessage;
import org.matsim.api.core.v01.events.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by salma_000 on 7/7/2017.
 */
public class EventReceiver extends UntypedActor {
    public static String ACTOR_NAME = "EventReceiver";
    private static long eventCount = 0;
    public List<ActorRef> actorRefList = new ArrayList<ActorRef>();
    private int numberOfActor = 2;

    @Override
    public void preStart() throws Exception {
        for (int i = 0; i < numberOfActor; i++) {
            actorRefList.add(getContext().actorOf(Props.create(EventConsumerActor.class), EventConsumerActor.ACTOR_NAME + i));
        }
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof Event) {
            eventCount++;
            int actorIndex = (int) eventCount % 2;
            actorRefList.get(actorIndex).tell(message, ActorRef.noSender());
        } else if (message instanceof EventSimCompleteMessage) {
            for (int i = 0; i < numberOfActor; i++) {
                ActorRef consumer = actorRefList.get(i);
                consumer.tell(message, ActorRef.noSender());
            }
        }
    }
}
