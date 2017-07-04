package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.listener;

import org.matsim.api.core.v01.events.LinkEnterEvent;

import java.util.List;

public interface ILinkEnterEventListener {
    void linkEnterEventCallBack(List<LinkEnterEvent> eventList);
}
