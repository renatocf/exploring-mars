package ml.renatocf.exploringmars.controllers

import org.scalatra._
import org.scalatra.json._
import org.scalatra.swagger._

import org.json4s.{DefaultFormats, Formats}
import org.json4s.ext.JavaTypesSerializers

import com.typesafe.scalalogging.LazyLogging

import ml.renatocf.exploringmars.data.DatabaseSessionSupport

class MapsController(implicit val swagger: Swagger)
  extends ScalatraServlet
     with JacksonJsonSupport
     with LazyLogging
     with SwaggerSupport
     with DatabaseSessionSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats ++ JavaTypesSerializers.all

  protected val applicationDescription = "The Maps API. It exposes operations for Maps."

  before() {
    contentType = formats("json")
  }

  get("/") {
    logger.info("Getting Maps")
    ("message" -> "Getting Maps")
  }
}
