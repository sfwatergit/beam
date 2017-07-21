package beam.playground.jdeqsimPerformance.akkaeventsim.events.handlers;

/**
 * Created by salma_000 on 7/20/2017.
 */

import org.matsim.core.events.handler.EventHandler;

public interface EventCountHandler extends EventHandler {
    int getCount();
}
