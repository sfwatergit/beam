package beam.playground.jdeqsimPerformance.akkaeventsim.events.handlers;

import org.matsim.api.core.v01.events.Event;

public interface LinkCountEventHandler extends EventCountHandler {
	public void handleEvent(Event event);
}

