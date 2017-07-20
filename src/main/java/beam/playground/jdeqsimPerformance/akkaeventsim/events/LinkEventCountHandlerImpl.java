package beam.playground.jdeqsimPerformance.akkaeventsim.events;

import beam.playground.jdeqsimPerformance.akkaeventsim.events.handlers.LinkCountEventHandler;
import org.matsim.api.core.v01.events.Event;

public class LinkEventCountHandlerImpl implements LinkCountEventHandler {


	int count = 0;

	public LinkEventCountHandlerImpl() {

	}

	@Override
	public void reset(int iteration) {

	}

	@Override
	public void handleEvent(Event event) {
		count++;
		//System.out.println("Got the event " + event.toString());
	}

	public int getCount(){
		return count;
	}



}

