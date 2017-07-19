package beam.agentsim.agents

import akka.actor.ActorRef
import beam.agentsim.scheduler.BeamAgentScheduler.{CompletionNotice, ScheduleTrigger}
import beam.agentsim.scheduler.Trigger

/**
  * BEAM
  */
trait TriggerShortcuts {
  def completed(triggerId: Long, scheduleTriggers: Vector[ScheduleTrigger] = Vector()): CompletionNotice = {
    CompletionNotice(triggerId, scheduleTriggers)
  }
  def schedule[T <: Trigger](tick: Double, agent: ActorRef)(implicit tag: scala.reflect.ClassTag[T]): Vector[ScheduleTrigger] = {
    Vector[ScheduleTrigger](scheduleOne(tick,agent))
  }
  def scheduleOne[T <: Trigger](tick: Double, agent: ActorRef)(implicit tag: scala.reflect.ClassTag[T]): ScheduleTrigger = {
    ScheduleTrigger(tag.runtimeClass.getConstructor(classOf[Double]).newInstance(new java.lang.Double(tick)).asInstanceOf[T], agent)
  }
}