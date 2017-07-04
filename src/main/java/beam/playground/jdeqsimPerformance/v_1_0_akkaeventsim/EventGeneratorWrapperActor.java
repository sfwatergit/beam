package beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim;


import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.GenerateEventMessage;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.StartEventGeneratorMessage;
import beam.playground.jdeqsimPerformance.v_1_0_akkaeventsim.messages.StopEventGeneratorMessage;
import scala.concurrent.duration.Duration;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by salma_000 on 7/4/2017.
 */
public class EventGeneratorWrapperActor extends UntypedActor {
    public static final String ACTOR_NAME = "EventGeneratorWrapperActor";
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    ActorRef bufferActor;
    private Map<Integer, Cancellable> jobIdVsCancellable = new Hashtable<>();

    public EventGeneratorWrapperActor(ActorRef bufferActor) {
        this.bufferActor = bufferActor;
    }

    @Override
    public void onReceive(Object message) throws Throwable {

        if (message instanceof StartEventGeneratorMessage) {
            StartEventGeneratorMessage msg = (StartEventGeneratorMessage) message;
            createScheduleJob(msg.getId(), msg.getTimeInMilliSec(), msg.isOneTimeJob(), msg.getEventType());
        } else if (message instanceof StopEventGeneratorMessage) {

            StopEventGeneratorMessage msg = (StopEventGeneratorMessage) message;
            jobIdVsCancellable.get(msg.getJobId()).cancel();

        }

    }

    private void createScheduleJob(int jobId, Long duration, boolean oneTimeJob, String eventType) {
        ActorRef ref = getContext().actorOf(Props.create(EventGeneratorActor.class, this.bufferActor));
        GenerateEventMessage generateEventMessage = new GenerateEventMessage(eventType);
        Cancellable cancellable = null;
        if (oneTimeJob) {
            cancellable = getContext().system().scheduler().scheduleOnce(
                    Duration.create(duration, TimeUnit.MILLISECONDS), ref, generateEventMessage,
                    getContext().system().dispatcher(), null);
        } else {
            cancellable = getContext().system().scheduler().schedule(
                    Duration.create(duration, TimeUnit.MILLISECONDS),
                    Duration.create(duration, TimeUnit.MILLISECONDS), ref, generateEventMessage,
                    getContext().system().dispatcher(), null);
        }
        if (jobIdVsCancellable.containsKey(jobId))
            jobIdVsCancellable.remove(jobId);
        jobIdVsCancellable.put(jobId, cancellable);

    }

}
