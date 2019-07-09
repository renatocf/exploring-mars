package ml.renatocf.exploringmars

import org.scalatra._

import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

import com.typesafe.scalalogging.LazyLogging

class ExploringMars extends ScalatraServlet with JacksonJsonSupport with LazyLogging {
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  get("/") {
    logger.info("Logging")
    ("message" -> "hello world")
  }
}
