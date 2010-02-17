package model


import collection.mutable.ArrayBuffer
import format.TwitterElement
import java.util.concurrent.ConcurrentHashMap
import java.util.Date

// In-memory state for a user... need a better name for this or need to refine
// what this class will actually do.
class User(name: String, id: Int) {

  class Mute(var until: Long, var terms: List[String])

  private val userMutes = new ConcurrentHashMap[String, Mute]
  private val mutedUsers = new ConcurrentHashMap[String, Long]
  private var ignoredTerms = List[(String, Long)]()

  def isTweetMuted(tweet: TwitterElement): Boolean = {
    val name = tweet.value("screen_name").toLowerCase
    val now = System.currentTimeMillis

    // Is this user globally muted?
    if (mutedUsers.contains(name)) {
      if (now < mutedUsers.get(name)) return true
    }

    // Is some term in the tweet globally ignored?
    val text = tweet.value("text").toLowerCase
    for (termUntil <- ignoredTerms) {
      if (now < termUntil._2 && text.contains(termUntil._1)) return true
    }

    // Is this term muted for this user?
    val mute = userMutes.get(name)
    if (mute != null) {

    }

    return false

  }

  def addMute(u: String, t: String, untilTime: Long) = {
    val name = if (u != null) u.toLowerCase else u
    val term = if (t != null) t.toLowerCase else t

    if (name != null && term == null) {
      // We are muting this user completely.
      mutedUsers.put(name, untilTime)
    }
    else if (name == null && term != null) {
      // We are globally ignoring the term
      ignoredTerms = (term, untilTime) :: ignoredTerms
    }
    else {
      // We are muting a term for a user
      userMutes.get(name) match {
        case null =>
          userMutes.put(name, new Mute(untilTime, List(term)))
        case m: Mute =>
          if (m.until < untilTime) {
            m.until = untilTime
          }

          m.terms = term :: m.terms
      }
    }
  }

}

