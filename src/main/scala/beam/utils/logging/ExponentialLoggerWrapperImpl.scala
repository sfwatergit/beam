package beam.utils.logging

import java.util.concurrent.ConcurrentHashMap

import beam.utils.logging.ExponentialLoggerWrapperImpl._
import org.slf4j.{Logger, LoggerFactory}

private class ExponentialLoggerWrapperImpl(name: String) extends LoggerWrapper {

  private type Func = String => Unit

  private val logger: Logger = LoggerFactory.getLogger(name)

  override def error(msgTemplate: String, args: Any*): Unit = {
    if (logger.isErrorEnabled) {
      processMessage(msgTemplate, args) { message =>
        logger.error(message, args.map(_.asInstanceOf[AnyRef]): _*)
      }
    }
  }

  def warn(msgTemplate: String, args: Any*): Unit = {
    if (logger.isWarnEnabled) {
      processMessage(msgTemplate, args) { message =>
        logger.warn(message, args.map(_.asInstanceOf[AnyRef]): _*)
      }
    }
  }

  override def info(msgTemplate: String, args: Any*): Unit = {
    if (logger.isInfoEnabled) {
      processMessage(msgTemplate, args) { message =>
        logger.info(message, args.map(_.asInstanceOf[AnyRef]): _*)
      }
    }
  }

  override def debug(msgTemplate: String, args: Any*): Unit = {
    if (logger.isDebugEnabled) {
      processMessage(msgTemplate, args) { message =>
        logger.debug(message, args.map(_.asInstanceOf[AnyRef]): _*)
      }
    }
  }

  private def processMessage(messageTemplate: String, args: Any*)(func: Func): Unit = {
    val newValue = messages.merge(messageTemplate, 1, (counter, incValue) => counter + incValue)
    if (isNumberPowerOfTwo(newValue)) {
      val newMessage = "[" + newValue + "] " + messageTemplate
      func(newMessage)
    }
  }

  override def reset(): Unit = messages.clear()

}

private object ExponentialLoggerWrapperImpl {

  def isNumberPowerOfTwo(number: Int): Boolean = {
    number > 0 && ((number & (number - 1)) == 0)
  }

  private val messages: ConcurrentHashMap[String, Int] = new ConcurrentHashMap[String, Int]()

}
