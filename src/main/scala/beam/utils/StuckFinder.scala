package beam.utils

import beam.agentsim.scheduler.BeamAgentScheduler.ScheduledTrigger
import beam.agentsim.scheduler.Trigger
import beam.sim.config.BeamConfig.Beam.Debug.StuckAgentDetection
import beam.utils.reflection.ReflectionUtils
import com.typesafe.scalalogging.LazyLogging

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

/** THIS CLASS IS NOT THREAD-SAFE!!! It's safe to use it inside actor, but not use the reference in Future and other threads..
  */
class StuckFinder(val cfg: StuckAgentDetection) extends LazyLogging {
  if (!cfg.enabled) {
    logger.info("StuckFinder is ** DISABLED **")
  } else {
    verifyTypesExist()
  }

  private val class2Helper: Map[Class[_], StuckFinderHelper[ScheduledTrigger]] = if (!cfg.enabled) {
    Map.empty
  } else {
    cfg.thresholds.map { option =>
      val clazz = Class.forName(option.triggerType)
      (clazz, new StuckFinderHelper[ScheduledTrigger])
    }.toMap
  }

  private val class2Threshold: Map[Class[_], Long] = if (!cfg.enabled) {
    Map.empty
  } else {
    cfg.thresholds.map { option =>
      val clazz = Class.forName(option.triggerType)
      logger.info("{} => {} ms", clazz, option.markAsStuckAfterMs)
      (clazz, option.markAsStuckAfterMs)
    }.toMap
  }

  def add(time: Long, st: ScheduledTrigger): Unit = {
    if (cfg.enabled) {
      class2Helper
        .get(toKey(st))
        .foreach { helper =>
          helper.add(time, st)
        }
    }
  }

  def removeByKey(st: ScheduledTrigger): Option[ValueWithTime[ScheduledTrigger]] = {
    if (cfg.enabled) {
      class2Helper
        .get(toKey(st))
        .flatMap { helper =>
          helper.removeByKey(st)
        }
    } else
      None
  }

  def detectStuckAgents(
    time: Long = System.currentTimeMillis()
  ): Seq[ValueWithTime[ScheduledTrigger]] = {
    @tailrec
    def detectStuckAgents0(
      helper: StuckFinderHelper[ScheduledTrigger],
      stuckAgents: ArrayBuffer[ValueWithTime[ScheduledTrigger]]
    ): Seq[ValueWithTime[ScheduledTrigger]] = {
      helper.removeOldest() match {
        case Some(oldest) =>
          val isStuck: Boolean = isStuckAgent(oldest.value, oldest.time, time)
          if (!isStuck) {
            // We have to add it back
            add(oldest.time, oldest.value)
            stuckAgents
          } else {
            stuckAgents += oldest
            detectStuckAgents0(helper, stuckAgents)
          }
        case None =>
          stuckAgents
      }
    }
    val result = ArrayBuffer.empty[ValueWithTime[ScheduledTrigger]]
    if (cfg.enabled) {
      class2Helper.values.foreach { helper =>
        detectStuckAgents0(helper, result)
      }
    }
    result
  }

  def isStuckAgent(st: ScheduledTrigger, startedAtMs: Long, currentTimeMs: Long): Boolean = {
    val diff = currentTimeMs - startedAtMs
    val threshold = class2Threshold.getOrElse(toKey(st), cfg.defaultTimeoutMs)
    val isStuck = diff > threshold
    if (isStuck) {
      logger.warn(s"$st is stuck. Diff: $diff ms, Threshold: $threshold ms")
    }
    isStuck
  }

  private def toKey(st: ScheduledTrigger): Class[_] = st.triggerWithId.trigger.getClass

  private def verifyTypesExist(): Unit = {
    // Make sure that those classes exist
    val definedTypes = cfg.thresholds.map { t =>
      // ClassNotFoundException  will be thrown if class does not exists or renamed or deleted
      Class.forName(t.triggerType)
    }

    val allSubClasses = new ReflectionUtils { val packageName = "beam.agentsim" }.classesOfType[Trigger]
    allSubClasses.diff(definedTypes).foreach { clazz =>
      logger.warn("There is no configuration for '{}'", clazz)
    }
  }
}
