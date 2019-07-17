package ml.renatocf.exploringmars.controllers

import org.scalatra._
import org.scalatra.json._
import org.scalatra.swagger._

import org.json4s.{DefaultFormats, Formats}
import org.json4s.ext.JavaTypesSerializers

import com.typesafe.scalalogging.LazyLogging

import ml.renatocf.exploringmars.data.DatabaseSessionSupport

class ProbesController(implicit val swagger: Swagger)
  extends ScalatraServlet
     with JacksonJsonSupport
     with LazyLogging
     with SwaggerSupport
     with DatabaseSessionSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats ++ JavaTypesSerializers.all

  protected val applicationDescription = "The Probes API. It exposes operations for Probes."

  before() {
    contentType = formats("json")
  }

  get("/") {
    logger.info("Getting Probes")
    ("message" -> "Getting Probes")
  }
}

