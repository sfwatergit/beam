package beam.playground.jdeqsim.akkaeventsampling;

import akka.actor.*;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorMessage;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorStartJobMessage;
import beam.playground.jdeqsim.akkaeventsampling.messages.SchedulerActorStopJobMessage;
import scala.concurrent.duration.Duration;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SchedulerActorUtil extends UntypedActor {
    public static final String ACTOR_NAME = "SchedulerUtilActor";
    private ActorRef eventRouter;
    private Map<Integer, Cancellable> jobIdVsCancellable = new Hashtable<>();

    public SchedulerActorUtil(ActorRef eventRouter) {
        this.eventRouter = eventRouter;
    }

    private void createScheduleJob(int jobId, Long duration, boolean oneTimeJob, String messageType) {
        ActorRef ref = getContext().actorOf(Props.create(SchedulerActor.class, this.eventRouter));
        final SchedulerActorMessage schedulerActorRequest =
                new SchedulerActorMessage(messageType);
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
            createScheduleJob(msg.getId(), msg.getTimeInMilliSec(), msg.isOneTimeJob(), msg.getMessageType());
        } else if (message instanceof SchedulerActorStopJobMessage) {

            //system.actorSelection("/user/*") ! msg
            ActorSelection actorSelection = getContext().system().actorSelection("/user/*");
            actorSelection.tell("SIM_COMPLETED", ActorRef.noSender());


            SchedulerActorStopJobMessage msg = (SchedulerActorStopJobMessage) message;
            jobIdVsCancellable.get(msg.getJobId()).cancel();

        }
    }
}
