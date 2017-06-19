package beam.playground.jdeqsimPerformance;

import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.core.utils.io.MatsimXmlWriter;

public class LogEnterLinkEvents implements LinkEnterEventHandler{

	MatsimXmlWriter writer;
	EventCSVWriter writer2;
	int counter = 0;

	public LogEnterLinkEvents(){
		//writer
		writer2 = new EventCSVWriter();
	}

	@Override
	public void reset(int iteration) {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleEvent(LinkEnterEvent event) {
		// TODO Use a writer to Log all the details of an event to a CSV file
		//System.out.println(event.toString());

		System.out.println(event.getAttributes() + ", counter -> " + counter);
		counter++;
		/* Todo
		* 	1. Create a CSV Writer
		* 	2. Write each event into a csv file
		* 	3.
		*/
		writer2.logEvent(event);
	}
}

