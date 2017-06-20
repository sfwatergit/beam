package beam.playground.jdeqsim.akkaeventsampling;

import beam.playground.jdeqsim.akkaeventsampling.Events.IEventListener;
import beam.playground.jdeqsimPerformance.EventCSVWriter;
import org.matsim.api.core.v01.events.Event;

import java.util.List;

public class CSVLinkEnterEventListener implements IEventListener {

    EventCSVWriter csvWriter = new EventCSVWriter();

    @Override
    public void callBack(List<Event> eventList) {
        System.out.println("This is from the callback. " + eventList);
        for (Event event : eventList) {
            //System.out.println("CSVLinkEnterEventListener->>>>>>>>>>>>>" + event.getEventType());

            csvWriter.logEvent(event);
        }
    }

    @Override
    public void simCompleted() {

        csvWriter.printLinkDataToCSV();
    }
}
