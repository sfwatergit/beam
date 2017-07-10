package beam.playground.jdeqsimPerformance.akkaeventsim.handlers;

import org.matsim.api.core.v01.events.Event;

public class LinkCountEventHandlerImpl implements LinkCountEventHandlerI {


	int count = 0;

	public LinkCountEventHandlerImpl(){

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

