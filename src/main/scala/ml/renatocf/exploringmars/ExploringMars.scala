package ml.renatocf.exploringmars.swagger

import java.util.UUID
import java.net.URL

import org.scalatra._
import org.scalatra.json._
import org.scalatra.swagger._

import org.json4s.DefaultFormats
import org.json4s.MappingException
import org.json4s.ext.EnumNameSerializer
import org.json4s.ext.JavaTypesSerializers
import org.json4s.jackson.Serialization

import com.typesafe.scalalogging.LazyLogging

import org.squeryl.PrimitiveTypeMode._

import ml.renatocf.exploringmars.models.Command
import ml.renatocf.exploringmars.models.Command._
import ml.renatocf.exploringmars.models.Direction
import ml.renatocf.exploringmars.models.Map
import ml.renatocf.exploringmars.models.MapInput
import ml.renatocf.exploringmars.models.Probe
import ml.renatocf.exploringmars.models.ProbeInput

import ml.renatocf.exploringmars.data.MarsDb
import ml.renatocf.exploringmars.data.DatabaseSessionSupport

class ExploringMarsApiDocs(implicit val swagger: Swagger)
  extends ScalatraServlet
     with JacksonSwaggerBase
     with LazyLogging
     with DatabaseSessionSupport {
  get("/"){
    redirect("/swagger.json")
  }

  post("/database"){
    transaction { MarsDb.create }
  }

  delete("/database"){
    transaction { MarsDb.drop }
  }

  get("/:slug"){
    redirect("/swagger.json")
  }
}

object ExploringMarsApiInfo extends ApiInfo(
    "The Exploring Mars API",
    "Docs for the Exploring Mars API",
    "http://exploringmars.renatocf.ml",
    "renato.cferreira@hotmail.com",
    "MIT",
    "http://opensource.org/licenses/MIT")

class ExploringMarsSwagger extends Swagger(Swagger.SpecVersion, "1.0.0", ExploringMarsApiInfo)
