package beam.playground.jdeqsimPerformance.akkaeventsim.events.handlers;

import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;

public interface LinkLeaveEventCountHandler extends LinkLeaveEventHandler {

	public int getCount();
}

