package beam.playground.jdeqsimPerformance.akkaeventsim.handlers;

import org.matsim.api.core.v01.events.LinkLeaveEvent;

public class LinkLeaveEventHandlerImpl implements LinkLeaveEventHandlerI {


	int count = 0;

	public LinkLeaveEventHandlerImpl(){

	}

	@Override
	public void reset(int iteration) {

	}

	@Override
	public void handleEvent(LinkLeaveEvent event) {
		count++;
		//System.out.println("Got the event " + event.toString());
	}

	public int getCount(){
		return count;
	}

}

