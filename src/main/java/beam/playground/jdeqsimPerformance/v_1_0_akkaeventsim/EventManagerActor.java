package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.RoundRobinPool;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.listener.IEventListener;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.listener.ILinkEnterEventListener;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.listener.ILinkLeaveEventListener;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.EventManagerMessage;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.EventManagerWorkerMessage;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Created by salma_000 on 7/4/2017.
 */
public class EventManagerActor extends UntypedActor {
    public static final String ACTOR_NAME = "EventManagerActor";
    public static ArrayList<ILinkEnterEventListener> linkEnterEventHandlerList = new ArrayList<ILinkEnterEventListener>(5);
    public static ArrayList<ILinkLeaveEventListener> linkLeaveEventHandlerList = new ArrayList<ILinkLeaveEventListener>(5);
    public static ArrayList<IEventListener> allEventHandlerList = new ArrayList<IEventListener>(5);
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private ActorRef worker;

    public static boolean registerEventListener(IEventListener listener) {
        return allEventHandlerList.add(listener);
    }

    public static boolean registerLinkEnterEventListener(ILinkEnterEventListener listener) {
        return linkEnterEventHandlerList.add(listener);
    }

    public static boolean registerLinkLeaveEventListener(ILinkLeaveEventListener listener) {
        return linkLeaveEventHandlerList.add(listener);
    }

    public void preStart() throws Exception {
        SupervisorStrategy supervisorStrategy = new OneForOneStrategy(5, Duration.create(1, TimeUnit.MINUTES), Collections.<Class<? extends Throwable>>singletonList(Exception.class));
        worker = getContext().actorOf(new RoundRobinPool(3).withSupervisorStrategy(supervisorStrategy).props(Props.create(Worker.class)), Worker.ACTOR_NAME);

    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof EventManagerMessage) {

            this.worker.forward(new EventManagerWorkerMessage((EventManagerMessage) message), getContext());
        }
    }
}
