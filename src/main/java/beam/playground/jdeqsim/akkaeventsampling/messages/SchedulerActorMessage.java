package beam.playground.jdeqsim.akkaeventsampling.messages;

import java.io.Serializable;


public class SchedulerActorMessage implements IRequest, Serializable {
    public static final String GENERATE_EVENT = "generateEvent";
    public static final String SPECIAL_EVENT = "specialEvent";
    private String messageType;

    public SchedulerActorMessage(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageType() {
        return messageType;
    }
}
