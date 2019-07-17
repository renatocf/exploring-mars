package ml.renatocf.exploringmars.swagger

import org.scalatra.ScalatraServlet
import org.scalatra.swagger.{ApiInfo, JacksonSwaggerBase, Swagger}

import com.typesafe.scalalogging.LazyLogging

class ExploringMarsApiDocs(implicit val swagger: Swagger) extends ScalatraServlet with JacksonSwaggerBase with LazyLogging {
  get("/"){
    redirect("/swagger.json")
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
