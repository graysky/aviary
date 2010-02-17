package db

import java.util.Date
import model.User


object UserDao {

  def buildUser(name: String): Option[User] = {
    None  
  }

  def lookupId(name: String): Option[Int] = {
    None
  }

  def addMute(name: String, term: String, until: Date) = {
    
  }
}