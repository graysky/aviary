package http

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.eclipse.jetty.server.Request
import org.apache.http.client.methods.{HttpDelete, HttpPost, HttpUriRequest, HttpGet}
import org.apache.http.impl.client.{BasicResponseHandler, DefaultHttpClient}
import sun.misc.BASE64Decoder
import collection.immutable.HashMap
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.message.BasicNameValuePair
import java.util.ArrayList
import org.apache.http.protocol.HTTP
import org.apache.http.params.{HttpProtocolParams, BasicHttpParams}
import util.matching.Regex

class ResponseFormat {

  def getResponseFormat(target: String) {
    if (target.contains(".json")) {
      ResponseFormat.JSON
    }
    else if (target.contains(".xml")) {
      ResponseFormat.XML
    }
    else {
      ResponseFormat.UNKNOWN
    }
  }
}
object ResponseFormat extends Enumeration {
  type ResponseFormat = Value
  val JSON, XML, UNKNOWN = Value
}

/**
 * Encapsulates a request from the user to the proxy.
 */
class TwitterRequest(target: String, request: HttpServletRequest) {

  var twitterResponse: TwitterResponse = null


  // Pull out the auth data
  var username = extractBasicAuthUser(request.getHeader("Authorization"))

  def sendRequest(root: String) = {
    val qs = request.getQueryString
    var url = root + target

    if (qs != null) {
      url += "?" + qs
    }

    // Make a client pointing at our root URL with the same query string
    val client = new DefaultHttpClient()
    val method = getHttpMethod(request, url)

    // Add headers
    val headerNames = request.getHeaderNames
    while (headerNames.hasMoreElements) {
      val name = headerNames.nextElement.toString

      // We'll set our own content length
      if (name != HTTP.CONTENT_LEN) {
        val headerVal = request.getHeader(name)
        method.addHeader(name, headerVal)
      }
    }

    // Send to twitter
    var response = client.execute(method)
    twitterResponse = new TwitterResponse(response)

    println(response.getStatusLine.toString)
    println(twitterResponse.getReadableBody)
    //println(EntityUtils.toString(response.getEntity))
    //response.getAllHeaders.foreach(h => println(h.getName + "=" + h.getValue))
  }

  private def getHttpMethod (request: HttpServletRequest, url: String): HttpUriRequest = {
    request.getMethod match {
      case "GET" => new HttpGet(url)
      case "POST" => createPost(request, url)
      case "DELETE" => new HttpDelete(url)
    }
  }

  private def createPost(request: HttpServletRequest, url: String): HttpPost = {
    val post = new HttpPost(url)

    // Add params
    val params = new ArrayList[BasicNameValuePair]

    val names = request.getParameterNames
    while (names.hasMoreElements) {
      val name = names.nextElement.toString
      val value = request.getParameter(name)

      params.add(new BasicNameValuePair(name, value))
    }

    post.setEntity(new UrlEncodedFormEntity(params))

    // Avoid error 417 Expection Failed
    val httpParams = new BasicHttpParams();
    HttpProtocolParams.setUseExpectContinue(httpParams, false)
    post.setParams(httpParams)

    post
  }

  private def extractBasicAuthUser(authString: String): Option[String] = {
    if (authString != null && authString.startsWith("Basic ")) {
      val decoder = new BASE64Decoder()
      val raw = new String(decoder.decodeBuffer(authString.substring("Basic ".length)))
      new Some((raw.substring(0, raw.indexOf(":"))))
    }
    else {
      None
    }
  }



}