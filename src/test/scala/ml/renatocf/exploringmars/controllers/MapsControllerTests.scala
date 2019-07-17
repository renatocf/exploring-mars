package ml.renatocf.exploringmars.controllers

import com.typesafe.scalalogging.LazyLogging

import org.scalatest.BeforeAndAfter

import org.scalatra.test.scalatest._

import org.squeryl.PrimitiveTypeMode._

import ml.renatocf.exploringmars.models.Map
import ml.renatocf.exploringmars.data.MarsDb
import ml.renatocf.exploringmars.data.DatabaseInit
import ml.renatocf.exploringmars.swagger.ExploringMarsSwagger

class MapsControllerTests extends ScalatraFlatSpec with DatabaseInit with BeforeAndAfter with LazyLogging {
  implicit val swagger = new ExploringMarsSwagger
  addServlet(new MapsController, "/maps/*")

  configureDb()

  before {
    transaction {
      MarsDb.drop
      MarsDb.create
    }
  }

  "The MapsController" should "return status 200 for GET /maps" in {
    get("/maps") {
      status shouldBe 200
    }
  }
}
