package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.eventprocessor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.EventManagerActor;


/**
 * Created by salma_000 on 7/4/2017.
 */
public class EventProcessActor extends UntypedActor {
    public static final String ACTOR_NAME = "EventManagerActor";
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private ActorRef eventManagerActor;

    public void preStart() throws Exception {
        this.eventManagerActor = getContext().actorOf(Props.create(EventManagerActor.class), EventManagerActor.ACTOR_NAME);
    }

    @Override
    public void onReceive(Object message) throws Throwable {

    }
}
