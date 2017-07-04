package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages;

import org.matsim.api.core.v01.events.Event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by salma_000 on 7/4/2017.
 */
public class ProcessActorMessage implements Serializable {
    private List<Event> eventList;

    public ProcessActorMessage(List<Event> eventList) {
        this.eventList = new ArrayList<>(eventList);
    }


}
