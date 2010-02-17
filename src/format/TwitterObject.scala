package format

import org.codehaus.jackson.map.TreeMapper
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.node.ArrayNode
import collection.mutable.{ArrayStack, Stack, ListBuffer}

trait TwitterDocument {
  def filter(path: String)(shouldStay : (JsonTwitterElement) => Boolean): Int
}

trait TwitterElement {
  def value(field: String): String
}

class JsonTwitterDocument(content: Array[byte], mapper: TreeMapper) extends TwitterDocument {
  private val json = mapper.readTree(content)

  override def filter(path: String)(shouldStay : (JsonTwitterElement) => Boolean): Int = {
    // TODO Allow more than one level
    var node = json.get(path)
    if (node == null) {
      // we didn't find it. this is the expected case for twitter's json which
      // does not have enclosing elements like the xml
      node = json;
    }

    val iterator = node.getElements
    var removals:ArrayStack[Int] = new ArrayStack
    val element = new JsonTwitterElement

    var index = 0;
    while (iterator.hasNext) {
      element.node = iterator.next

      if (!shouldStay(element)) {
        removals += index
      }

      index += 1
    }

    node match {
      case n: ArrayNode => removals.foreach(r => n.remove(r))
      case _ => throw new Exception("Can't remove from " + node.getClass)
    }

    removals.size
  }
}

class JsonTwitterElement extends TwitterElement {
  var node: JsonNode = null

  // TODO Allow more than one level
  override def value(field: String): String = {
    node.get(field).getTextValue
  }
}

