package model


import collection.mutable.ArrayBuffer
import format.TwitterElement
import java.util.concurrent.ConcurrentHashMap
import java.util.Date

// In-memory state for a user... need a better name for this or need to refine
// what this class will actually do.
class User(name: String, id: Int) {

  case class Mute(val term: String, val until: Long)

  private val userMutes = new ConcurrentHashMap[String, List[Mute]]
  private val mutedUsers = new ConcurrentHashMap[String, Long]
  private var ignoredTerms = List[Mute]()

  def isTweetMuted(tweet: TwitterElement): Boolean = {
    val name = tweet.value("screen_name").toLowerCase
    val now = System.currentTimeMillis

    // Is this user globally muted?
    if (mutedUsers.contains(name)) {
      if (now < mutedUsers.get(name)) return true
    }

    // Is some term in the tweet globally ignored?
    val text = tweet.value("text").toLowerCase
    for (mute <- ignoredTerms) {
      if (now < mute.until && text.contains(mute.term)) return true
    }

    // Is this term muted for this user?
    val mutes = userMutes.get(name)
    if (mutes != null) {
      for (mute <- mutes) {
        if (now < mute.until && text.contains(mute.term)) return true
      }
    }

    // This tweet wasn't muted
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
      ignoredTerms = Mute(term, untilTime) :: ignoredTerms
    }
    else {
      // We are muting a term for a user
      userMutes.get(name) match {
        case null =>
          userMutes.put(name, List(Mute(term, untilTime)))
        case mutes =>
          userMutes.put(name, Mute(term, untilTime) :: mutes)
      }
    }
  }

}

