package beam.playground.ajdeqsim.v1.scheduler;

import akka.actor.ActorRef;
import beam.playground.ajdeqsim.v1.messages.Message;
import beam.playground.beamSimAkkaProtoType.scheduler.TriggerMessage;

public class TriggerMessageWithPayload extends TriggerMessage{

	private Message payloadMessage; 
	
	
	public TriggerMessageWithPayload(ActorRef destinationActorRef, double time, int priority,Message payloadMessage) {
		super(destinationActorRef, time, priority);
		this.setPayloadMessage(payloadMessage);
	}


	public Message getPayloadMessage() {
		return payloadMessage;
	}


	private void setPayloadMessage(Message payloadMessage) {
		this.payloadMessage = payloadMessage;
	}

}
