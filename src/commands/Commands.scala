package commands

import model.User


trait Command {
  def execute(parsedCommand: ParsedCommand, user: User)
}

class MuteCommand extends Command {
  override def execute(parsedCommand: ParsedCommand, user: User) = {
    /*
    val until = parsedCommand.timeframe match {
      case l: Long => l
      case None => null
    }

    if (parsedCommand.usernames.isEmpty) {
      // Global mutes
      
    }
    else {
      
    }

    for (name <- parsedCommand.usernames) {
      // These are wildcard mutes
      for (term <- parsedCommand.terms) {
        user.addMute("*")
      }
    }
    */
    
  }
}