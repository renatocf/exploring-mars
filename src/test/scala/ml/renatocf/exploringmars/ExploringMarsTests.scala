package ml.renatocf.exploringmars

import org.scalatra.test.scalatest._
import org.scalatest.BeforeAndAfter

import ml.renatocf.exploringmars.data.DatabaseInit
import ml.renatocf.exploringmars.data.MarsDb

class ExploringMarsTests extends ScalatraFunSuite with DatabaseInit with BeforeAndAfter {
  addServlet(classOf[ExploringMars], "/*")

  before {
    configureDb()
  }

  after {
    closeDbConnection()
  }

  test("GET / on ExploringMars should return status 200") {
    get("/") {
      status should equal (200)
    }
  }
}
