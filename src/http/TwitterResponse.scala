package http

import org.apache.http.util.EntityUtils
import org.apache.http.{Header, HttpResponse}
import java.util.zip.GZIPInputStream
import java.io.ByteArrayInputStream
import io.Source

// Wrap a response from Twitter
class TwitterResponse(var httpResponse: HttpResponse) {

  // We might be asked for the gzipped response or the raw response
  private var gzipped = false
  private var rawBytes: Array[Byte] = null
  private var readableBody: String = null

  init()

  private def init() = {
    val encoding = httpResponse.getEntity.getContentEncoding

    if (encoding != null) {
      val codecs = encoding.getElements
      if (codecs != null) {
        val codec = codecs.find { codec => codec.getName.equalsIgnoreCase("gzip") }
        codec.map { h => gzipped = true }
      }
    }

    rawBytes = EntityUtils.toByteArray(httpResponse.getEntity)

    if (!gzipped) {
      readableBody = new String(rawBytes)
    }
  }

  def isGzipped = gzipped

  def replaceResponseBody(readableString: String) = {
    // todo
  }



  def getReadableBody: String = {
    if (gzipped && readableBody == null) {
      val stream = new GZIPInputStream(new ByteArrayInputStream(rawBytes))

      // Read it, break it into lines, combine them.
      readableBody = Source.fromInputStream(stream).getLines.foldLeft("")(_ + _)
      stream.close
    }

    readableBody
  }

  def getStatusCode: Int = httpResponse.getStatusLine.getStatusCode

  def getHeaders: Array[Header] = httpResponse.getAllHeaders

  def getResponseBytes = rawBytes


}