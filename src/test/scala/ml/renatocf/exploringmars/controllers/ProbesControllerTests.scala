package ml.renatocf.exploringmars.controllers

import com.typesafe.scalalogging.LazyLogging

import org.scalatest.BeforeAndAfter

import org.scalatra.test.scalatest._

import org.squeryl.PrimitiveTypeMode._

import ml.renatocf.exploringmars.models.Map
import ml.renatocf.exploringmars.data.MarsDb
import ml.renatocf.exploringmars.data.DatabaseInit
import ml.renatocf.exploringmars.swagger.ExploringMarsSwagger

class ProbesControllerTests extends ScalatraFlatSpec with DatabaseInit with BeforeAndAfter with LazyLogging {
  implicit val swagger = new ExploringMarsSwagger
  addServlet(new ProbesController, "/probes/*")

  configureDb()

  before {
    transaction {
      MarsDb.drop
      MarsDb.create
    }
  }

  "The ProbesController" should "return status 200 for GET /probes" in {
    get("/probes") {
      status shouldBe 200
    }
  }
}
