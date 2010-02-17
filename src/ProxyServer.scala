import com.sun.net.httpserver.{HttpHandler}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.eclipse.jetty.server.{Request, Server}

/**
 * Starts up an instance of the server - twitter and admin handlers
 */
class ProxyServer(val userPort: Int, val adminPort: Int) {

  private var userServer: Server = new Server(userPort)
  private var adminServer: Server = new Server(adminPort)

  def start = {
    userServer.setHandler(new TwitterHandler)
    adminServer.setHandler(new AdminHandler)

    userServer.start
    adminServer.start
  }

  def join = {
    userServer.join
    adminServer.join
  }
  
}

object ProxyServerRunner {
  def main(args: Array[String]) = {
    var userPort = 8000
    var adminPort = 9090

    if (args.length > 0) {
      userPort = args(0).toInt
    }

    if (args.length > 1) {
      adminPort = args(1).toInt
    }

    val server = new ProxyServer(userPort, adminPort)
    server.start
    server.join
  }
}