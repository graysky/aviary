package commands

import model.User


trait Command {
  def execute(parsedCommand: ParsedCommand, user: User)
}

class MuteCommand extends Command {
  override def execute(parsedCommand: ParsedCommand, user: User) = {

    val until = parsedCommand.timeframe match {
      case None => java.lang.Long.MAX_VALUE
      case Some(t) => t.milliseconds
    }

    if (parsedCommand.usernames.isEmpty) {
      // Global mutes
      for (term <- parsedCommand.terms) {
        user.addMute(null, term, until)
      }
    }
    else {
      // Mute specific users
      for (username <- parsedCommand.usernames) {

      }
    }
  }
}