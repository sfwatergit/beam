package beam.playground.ajdeqsim.v1.messageHandler;

import java.util.HashSet;

import akka.actor.UntypedActor;
import beam.playground.ajdeqsim.v1.messages.DeadlockPreventionMessage;
import beam.playground.ajdeqsim.v1.scheduler.TriggerMessageWithPayload;
import beam.playground.beamSimAkkaProtoType.scheduler.TriggerMessage;
import beam.utils.DebugLib;

public class MessageHandlerActor extends UntypedActor {

	HashSet<DeadlockPreventionMessage> cancelledDeadlockMessages=new HashSet<>();
	
	@Override
	public void onReceive(Object message) throws Throwable {
		
		if (message instanceof TriggerMessageWithPayload){
			// TODO: process message here differently for each type?
			
			boolean filterMessage=false;
			
			TriggerMessageWithPayload triggerMessage=(TriggerMessageWithPayload) message;
			
			if (triggerMessage.getPayloadMessage() instanceof DeadlockPreventionMessage) {
				if (cancelledDeadlockMessages.contains(triggerMessage.getPayloadMessage())){
					filterMessage=true;
					cancelledDeadlockMessages.remove(triggerMessage.getPayloadMessage());
				}
			} 
			
			if (!filterMessage){
				((TriggerMessage) message).getAgentRef().tell(message, getSelf());
			}
		} else if (message instanceof DeadlockPreventionMessage){
			cancelledDeadlockMessages.add((DeadlockPreventionMessage) message);
		}else {
			DebugLib.stopSystemAndReportInconsistency();
		}
		

		
	}

}
