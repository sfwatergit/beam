package beam.playground.jdeqsimPerformance.akkaeventsim.handlers;

import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;

public class LinkEnterEventHandlerImpl implements LinkEnterEventHandler {



	public LinkEnterEventHandlerImpl(){

	}

	@Override
	public void reset(int iteration) {

	}

	@Override
	public void handleEvent(LinkEnterEvent event) {
		System.out.println("Got the event " + event.toString());
	}


}

