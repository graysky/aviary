import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.Request

  /**
   * Handle admin requests
   */
  class AdminHandler extends AbstractHandler {
    override def handle(target: String, req:Request, request: HttpServletRequest, response: HttpServletResponse) = {


    }
  }