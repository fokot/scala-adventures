package typeclasses

sealed  trait JsonValue
case class JsonObject(entries: Map[String, JsonValue]) extends JsonValue
case class JsonArray(entries: Seq[JsonValue])          extends JsonValue
case class JsonString(value: String)                   extends JsonValue
case class JsonNumber(value: BigDecimal)               extends JsonValue
case class JsonBoolean(value: Boolean)                 extends JsonValue
case object JsonNull                                   extends JsonValue


trait Json[A] {
  /** aaa **/
  def json(value: A): JsonValue
}

object JsonWriter {
  def write(value: JsonValue): String = value match {
    case JsonObject(entries) =>
      "{" +  (for ((key, value) <- entries) yield key +  ": " + write(value)) + "}"
    case JsonArray(entries) =>
      "[" + (entries map write) mkString ", " + "]"
    case JsonString(value) => "\"" + value + "\""
    case JsonNumber(value) => value.toString
    case JsonBoolean(value) => value.toString
    case JsonNull => "null"
  }
}