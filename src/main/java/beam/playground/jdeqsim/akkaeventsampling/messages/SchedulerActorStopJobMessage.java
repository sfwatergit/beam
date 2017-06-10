package beam.playground.jdeqsim.akkaeventsampling.messages;

import java.io.Serializable;

public class SchedulerActorStopJobMessage implements IRequest, Serializable {
    private int jobId;

    public SchedulerActorStopJobMessage(int id) {
        this.jobId = id;
    }

    public int getJobId() {
        return jobId;
    }
}
