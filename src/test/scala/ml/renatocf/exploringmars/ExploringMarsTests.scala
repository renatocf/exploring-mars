package ml.renatocf.exploringmars

import org.scalatra.test.scalatest._

class ExploringMarsTests extends ScalatraFunSuite {

  addServlet(classOf[ExploringMars], "/*")

  test("GET / on ExploringMars should return status 200") {
    get("/") {
      status should equal (200)
    }
  }

}
