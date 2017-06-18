package beam.playground.jdeqsim.akkaeventsampling;

import beam.playground.jdeqsim.akkaeventsampling.Events.IEventListener;
import org.matsim.api.core.v01.events.Event;

import java.util.List;

public class CSVLinkEnterEventListener implements IEventListener {
    @Override
    public void callBack(List<Event> eventList) {
        for (Event event : eventList) {
            System.out.println("CSVLinkEnterEventListener->>>>>>>>>>>>>" + event.getEventType());

        }
    }
}
