package http

import javax.servlet.http.HttpServletResponse

// Send a response back to the client
class ClientResponse(var twitterResponse: TwitterResponse) {

  def sendResponse(clientResponse: HttpServletResponse) = {

    // Add headers and status, then send out the response bytes
    clientResponse.setStatus(twitterResponse.getStatusCode)
    twitterResponse.getHeaders.foreach(h => clientResponse.addHeader(h.getName, h.getValue))

    if (twitterResponse.isGzipped) {
      clientResponse.setCharacterEncoding("gzip")
    }

    clientResponse.getOutputStream.write(twitterResponse.getResponseBytes)
  }

}