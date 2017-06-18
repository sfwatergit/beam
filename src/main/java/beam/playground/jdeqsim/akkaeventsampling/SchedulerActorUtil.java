package beam.playground.jdeqsim.akkaeventsampling;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.actor.UntypedActor;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorMessage;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorStartJobMessage;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorStopJobMessage;
import scala.concurrent.duration.Duration;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SchedulerActorUtil extends UntypedActor {
    public static final String ACTOR_NAME = "SchedulerUtilActor";
    private ActorRef eventLoadBalancer;
    private Map<Integer, Cancellable> jobIdVsCancellable = new Hashtable<>();

    public SchedulerActorUtil(ActorRef eventLoadBalancer) {
        this.eventLoadBalancer = eventLoadBalancer;
    }

    private void createScheduleJob(int jobId, Long duration, boolean oneTimeJob, String eventType, long syncStartTime, long binSize) {
        ActorRef ref = getContext().actorOf(Props.create(SchedulerActor.class, this.eventLoadBalancer));
        SchedulerActorMessage schedulerActorRequest = null;

        if (syncStartTime != -1 && binSize != -1) {
            schedulerActorRequest = new SchedulerActorMessage(eventType, syncStartTime, binSize);
        } else {
            schedulerActorRequest = new SchedulerActorMessage(eventType);
        }
        Cancellable cancellable = null;
        if (oneTimeJob) {
            cancellable = getContext().system().scheduler().scheduleOnce(
                    Duration.create(duration, TimeUnit.MILLISECONDS), ref, schedulerActorRequest,
                    getContext().system().dispatcher(), null);
        } else {
            cancellable = getContext().system().scheduler().schedule(
                    Duration.create(duration, TimeUnit.MILLISECONDS),
                    Duration.create(duration, TimeUnit.MILLISECONDS), ref, schedulerActorRequest,
                    getContext().system().dispatcher(), null);
        }
        if (jobIdVsCancellable.containsKey(jobId))
            jobIdVsCancellable.remove(jobId);
        jobIdVsCancellable.put(jobId, cancellable);

    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof SchedulerActorStartJobMessage) {
            SchedulerActorStartJobMessage msg = (SchedulerActorStartJobMessage) message;
            createScheduleJob(msg.getId(), msg.getTimeInMilliSec(), msg.isOneTimeJob(), msg.getEventType(), msg.getSyncStartTime(), msg.getBinSize());
        } else if (message instanceof SchedulerActorStopJobMessage) {

            SchedulerActorStopJobMessage msg = (SchedulerActorStopJobMessage) message;
            jobIdVsCancellable.get(msg.getJobId()).cancel();

        }
    }
}
