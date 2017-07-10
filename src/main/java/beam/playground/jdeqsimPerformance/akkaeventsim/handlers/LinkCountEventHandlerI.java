package beam.playground.jdeqsimPerformance.akkaeventsim.handlers;

import org.matsim.api.core.v01.events.Event;
import org.matsim.core.events.handler.EventHandler;

public interface LinkCountEventHandlerI extends EventHandler {

	public void handleEvent(Event event);
	public int getCount();
}

