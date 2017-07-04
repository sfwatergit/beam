package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.listener;

import org.matsim.api.core.v01.events.Event;

import java.util.List;

/**
 * Created by salma_000 on 7/4/2017.
 */
public interface IEventListener {
    void linkLeaveEventCallBack(List<Event> eventList);
}
