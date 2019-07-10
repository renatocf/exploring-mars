import org.scalatra.LifeCycle
import javax.servlet.ServletContext

import ml.renatocf.exploringmars.ExploringMars
import ml.renatocf.exploringmars.data.DatabaseInit

class ScalatraBootstrap extends LifeCycle with DatabaseInit {
  override def init(context: ServletContext) {
    configureDb()
    context.mount(new ExploringMars, "/*")
  }

  override def destroy(context:ServletContext) {
    closeDbConnection()
  }
}
