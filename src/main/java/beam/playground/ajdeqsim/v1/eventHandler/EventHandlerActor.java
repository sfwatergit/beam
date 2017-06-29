package beam.playground.ajdeqsim.v1.eventHandler;

import java.util.List;

import org.matsim.api.core.v01.events.Event;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.handler.EventHandler;

import akka.actor.UntypedActor;
import beam.playground.ajdeqsim.v1.messages.Message;
import beam.playground.ajdeqsim.v1.scheduler.TriggerMessageWithPayload;
import beam.utils.DebugLib;

public class EventHandlerActor extends UntypedActor {

	EventsManager eventsManager; // TODO: replace through actor system
	
	@Override
	public void onReceive(Object message) throws Throwable {
		if (message instanceof TriggerMessageWithPayload){
			TriggerMessageWithPayload triggerMessageWP=(TriggerMessageWithPayload) message;
			Message payloadMessage=triggerMessageWP.getPayloadMessage();
			
			List<Event> events = payloadMessage.getEvents();
			
			if (events!=null){
				for (Event event:events){
					eventsManager.processEvent(event);
				}
			}
			
		} else {
			DebugLib.stopSystemAndReportInconsistency();
		}
	}

}
