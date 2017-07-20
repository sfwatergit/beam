package beam.playground.jdeqsimPerformance.akkaeventsim.events.handlers;

import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;

public interface LinkEnterEventCountHandler extends LinkEnterEventHandler {

	public int getCount();
}

