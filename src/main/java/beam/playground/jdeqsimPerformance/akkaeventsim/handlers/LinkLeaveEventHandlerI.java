package beam.playground.jdeqsimPerformance.akkaeventsim.handlers;

import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;

public interface LinkLeaveEventHandlerI extends LinkLeaveEventHandler{

	public int getCount();
}

