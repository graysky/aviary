import http.{ClientResponse, TwitterRequest}
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.Request

  /**
   * Handle proxied HTTP requests for twitter
   */
  class TwitterHandler extends AbstractHandler {

    private val apiRoot = "http://twitter.com"

    override def handle(target: String, req:Request, request: HttpServletRequest, response: HttpServletResponse) = {

      try {
        var proxiedRequest = new TwitterRequest(target, request)
        proxiedRequest.sendRequest(apiRoot)

        new ClientResponse(proxiedRequest.twitterResponse).sendResponse(response)
      }
      catch {
        case e: Exception => {
          println("Exception: " + e.getMessage + " ")
          e.printStackTrace
        }
      }

      req.setHandled(true)
    }

  }