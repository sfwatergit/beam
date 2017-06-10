package beam.playground.jdeqsim.akkaeventsampling.messages;

import java.io.Serializable;


public class SchedulerActorStartJobMessage implements IRequest, Serializable {

    private static int identifier = 0;
    private long timeInMilliSec = 5;
    private boolean oneTimeJob = false;
    private String messageType;
    private int id;

    public SchedulerActorStartJobMessage(long timeInMilliSec, String messageType) {
        this.timeInMilliSec = timeInMilliSec;
        this.messageType = messageType;
        id = identifier++;

    }

    public SchedulerActorStartJobMessage(long timeInMilliSec, boolean oneTimeJob) {
        this.timeInMilliSec = timeInMilliSec;
        this.oneTimeJob = oneTimeJob;
        id = identifier++;
    }

    public String getMessageType() {
        return messageType;
    }

    public int getId() {
        return id;
    }

    public long getTimeInMilliSec() {
        return timeInMilliSec;
    }

    public boolean isOneTimeJob() {
        return oneTimeJob;
    }
}
