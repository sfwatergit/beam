package beam.playground.jdeqsimPerformance.akkaeventsim.events;

import beam.playground.jdeqsimPerformance.akkaeventsim.events.handlers.LinkEnterEventCountHandler;
import org.matsim.api.core.v01.events.LinkEnterEvent;

public class LinkEnterEventCountHandlerImpl implements LinkEnterEventCountHandler {


	int count = 0;

	public LinkEnterEventCountHandlerImpl() {

	}

	@Override
	public void reset(int iteration) {

	}

	@Override
	public void handleEvent(LinkEnterEvent event) {
		count++;
		//System.out.println("Got the event " + event.toString());
	}

	public int getCount(){
		return count;
	}

}

