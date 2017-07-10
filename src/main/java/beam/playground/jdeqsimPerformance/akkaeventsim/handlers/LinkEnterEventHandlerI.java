package beam.playground.jdeqsimPerformance.akkaeventsim.handlers;

import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;

public interface LinkEnterEventHandlerI extends LinkEnterEventHandler {

	public int getCount();
}

