package http

import org.apache.http.entity.HttpEntityWrapper
import java.io.InputStream
import java.util.zip.GZIPInputStream
import org.apache.http.{HttpResponse, HttpResponseInterceptor, HttpEntity}
import org.apache.http.protocol.HttpContext

// An HttpEntity that decompressed gzipped content
class GzippedEntity(entity: HttpEntity) extends HttpEntityWrapper(entity: HttpEntity) {

  override def getContent: InputStream = {
    new GZIPInputStream(wrappedEntity.getContent)
  }

}

// Intercept HTTP requests and replace entities with
// gzipped ones if necessary.
class GzipInterceptor extends HttpResponseInterceptor {

  def process(response: HttpResponse, context: HttpContext) = {

    val entity = response.getEntity
    val encoding = entity.getContentEncoding

    if (encoding != null) {

      val codecs = encoding.getElements
      if (codecs != null) {
        val codec = codecs.find { codec => codec.getName.equalsIgnoreCase("gzip") }
        codec.map { h => response.setEntity(new GzippedEntity(entity)) }
      }
    }

  }

}