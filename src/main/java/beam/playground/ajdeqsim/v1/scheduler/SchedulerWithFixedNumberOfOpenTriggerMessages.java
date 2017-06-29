package beam.playground.ajdeqsim.v1.scheduler;

import java.util.PriorityQueue;
import java.util.SortedSet;

import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.mobsim.jdeqsim.Message;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import beam.utils.DebugLib;
import beam.utils.IntegerValueHashMap;
import beam.playground.ajdeqsim.v1.messages.DeadlockPreventionMessage;
import beam.playground.beamSimAkkaProtoType.GlobalLibAndConfig;
import beam.playground.beamSimAkkaProtoType.beamPersonAgent.ActStartMessage;
import beam.playground.beamSimAkkaProtoType.beamPersonAgent.ActivityEndMessage;
import beam.playground.beamSimAkkaProtoType.beamPersonAgent.BeamPersonAgent;
import beam.playground.beamSimAkkaProtoType.scheduler.StartSimulationMessage;
import beam.playground.beamSimAkkaProtoType.scheduler.TriggerAckMessage;
import beam.playground.beamSimAkkaProtoType.scheduler.TriggerMessage;

public class SchedulerWithFixedNumberOfOpenTriggerMessages extends UntypedActor {

	ActorRef messageHandlerActor;
	ActorRef eventHandlerActor; // TODO: we could have several instances of this
								// if this becomes a bottleneck

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private PriorityQueue<TriggerMessage> triggers = new PriorityQueue<TriggerMessage>();
	IntegerValueHashMap<Integer> numberOfResponsesPending = new IntegerValueHashMap();
	// key: tick, value: number of pending messages in that tick

	private int maxNumberOfOpenTriggerMessages;

	private int numberOfOpenTriggerMessages;

	public SchedulerWithFixedNumberOfOpenTriggerMessages(Population population, int maxNumberOfOpenTriggerMessages) {
		this.maxNumberOfOpenTriggerMessages = maxNumberOfOpenTriggerMessages;
		int i = 0;
		for (Person person : population.getPersons().values()) {
			Activity act = (Activity) person.getSelectedPlan().getPlanElements().get(0);
			double actEndTime = act.getEndTime();
			ActorRef personRef = getContext().actorOf(Props.create(BeamPersonAgent.class, person.getSelectedPlan(), getSelf()),
					"beamPersonAgent-" + i++);

			triggers.add(new ActivityEndMessage(personRef, actEndTime, 0));
		}
	}

	private void trySendingOutNewTriggerMessages() {
		while (triggers.size() > 0 && numberOfOpenTriggerMessages < maxNumberOfOpenTriggerMessages) {
			TriggerMessage trigger = triggers.poll();
			// trigger.getAgentRef().tell(trigger, getSelf());
			messageHandlerActor.tell(trigger, getSelf());
			eventHandlerActor.tell(trigger, getSelf());
			numberOfOpenTriggerMessages++;
		}
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		GlobalLibAndConfig.printMessage(log, message);
		updateStats(message);
		if (message instanceof StartSimulationMessage) {
			trySendingOutNewTriggerMessages();
		} else if (message instanceof TriggerMessage) {
			triggers.add((TriggerMessage) message);
		} else if (message instanceof TriggerAckMessage) {
			TriggerAckMessage triggerAckMessage = (TriggerAckMessage) message;
			processTriggerAck(triggerAckMessage);
			trySendingOutNewTriggerMessages();
			detectIfSimulationEndReached();
		} else {

			DebugLib.stopSystemAndReportInconsistency("unexpected message type received:" + message);
		}
	}

	private int stats_numberOfTriggerMessages = 0;
	private int stats_numberOfTriggerMessagesModulo = 1;

	private void updateStats(Object message) {
		if (message instanceof TriggerAckMessage) {
			stats_numberOfTriggerMessages++;
			if (stats_numberOfTriggerMessages % stats_numberOfTriggerMessagesModulo == 0) {
				log.info("numberOfTriggerMessages processed: " + stats_numberOfTriggerMessages);
				stats_numberOfTriggerMessagesModulo *= 2;
			}
		}
	}

	private void processTriggerAck(TriggerAckMessage triggerAckMessage) {
		numberOfOpenTriggerMessages--;

		scheduleTriggerMessage(triggerAckMessage);
	}

	private void scheduleTriggerMessage(TriggerAckMessage triggerAckMessage) {
		for (TriggerMessage triggerMessage : triggerAckMessage.getNextTriggerMessageToSchedule()) {
			try {
				triggers.add(triggerMessage);
			} catch (RuntimeException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private void detectIfSimulationEndReached() {
		if (triggers.size() == 0 && numberOfOpenTriggerMessages == 0) {
			log.info("end of simulation reached");
		}

	}

}
