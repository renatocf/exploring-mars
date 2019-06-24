package ml.renatocf.exploringmars

import org.scalatra._

class ExploringMars extends ScalatraServlet {

  get("/") {
    views.html.hello()
  }

}
