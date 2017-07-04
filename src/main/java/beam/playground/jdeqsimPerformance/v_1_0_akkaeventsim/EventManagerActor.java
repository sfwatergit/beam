package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.listener.IEventListener;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.listener.ILinkEnterEventListener;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.listener.ILinkLeaveEventListener;

import java.util.ArrayList;

/**
 * Created by salma_000 on 7/4/2017.
 */
public class EventManagerActor extends UntypedActor {
    public static final String ACTOR_NAME = "EventManagerActor";
    protected static ArrayList<ILinkEnterEventListener> linkEnterEventHandlerList = new ArrayList<ILinkEnterEventListener>(5);
    protected static ArrayList<ILinkLeaveEventListener> linkLeaveEventHandlerList = new ArrayList<ILinkLeaveEventListener>(5);
    protected static ArrayList<IEventListener> genericEventHandlerList = new ArrayList<IEventListener>(5);
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public static boolean registerEventListener(IEventListener listener) {
        return genericEventHandlerList.add(listener);
    }

    public static boolean registerLinkEnterEventListener(ILinkEnterEventListener listener) {
        return linkEnterEventHandlerList.add(listener);
    }

    public static boolean registerLinkLeaveEventListener(ILinkLeaveEventListener listener) {
        return linkLeaveEventHandlerList.add(listener);
    }


    @Override
    public void onReceive(Object message) throws Throwable {

    }
}
