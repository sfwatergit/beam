package beam.playground.jdeqsimPerformance.akkaeventsim.handlers;

import org.matsim.api.core.v01.events.LinkEnterEvent;

public class LinkEnterEventHandlerImpl implements LinkEnterEventHandlerI {


	int count = 0;

	public LinkEnterEventHandlerImpl(){

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
