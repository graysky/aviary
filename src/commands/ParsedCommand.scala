package commands

import scala._
import collection.mutable.{ArrayBuffer, ListBuffer}


class ParsedCommand {
  var usernames = defaultUsernames
  var terms = new ListBuffer[String]
  var timeframe: Option[TimeFrame] = defaultTimeframe

  def parse(cmd: String) = {
    val tokens = new ArrayBuffer[String]
    tokens.appendAll(cmd.split(" "))

    // We already know what command we are. Remove the first token.
    tokens.remove(0)

    if (timeframe != null) {
      timeframe = TimeFrame.parse(tokens)
    }

    // Now look for a list of usernames
    if (usernames != null) {
      usernames.clear
      usernames.appendAll(tokens.filter { token => token.startsWith("@")})
      tokens.trimStart(usernames.length)
    }

    // Finally collect up the terms. Re-split the remainder by comma
    terms.clear
    terms.appendAll(tokens.foldLeft("")(_ + " " + _).split(",").map(_.trim()))        
  }

  // Override and return null to disable username collection
  protected def defaultUsernames = new ListBuffer[String]

  // Override and return null to disable timeframe collection
  protected def defaultTimeframe: Option[TimeFrame] = None

}

// Timeframes
object TimeFrame {
  val PERIODS = Map("seconds" -> 1, "minutes" -> 60, "hours" -> 60 * 60,
                    "second" -> 1, "minute" -> 60, "hour" -> 60 * 60,
                    "days" -> 24 * 60 * 60, "weeks" -> 7 * 24 * 60 * 60,
                    "day" -> 24 * 60 * 60, "week" -> 7 * 24 * 60 * 60)

  def parse(tokens: ArrayBuffer[String]): Option[TimeFrame] = {
    val last = tokens.last.toLowerCase
    var opt: Option[TimeFrame] = None

    if (tokens.length > 1) {
      for ((period, value) <- PERIODS) {
        if (last == period) {
          val num = tokens(tokens.length - 2)
          // TODO Can I do this with a match?
          val numInt = try {
            num.toInt
          }
          catch {
            case _ => -1
          }

          if (numInt > 0) {
            tokens.trimEnd(2)
            opt = Some(new TimeFrame(num, period, numInt * value * 10000))
          }
        }
      }
    }

    opt
  }
}

class TimeFrame(val num: String, val period: String, val milliseconds: Long) {
  override def toString = {
    num + " " + period  
  }
}

class CommandParseException(msg: String) extends Exception {

}