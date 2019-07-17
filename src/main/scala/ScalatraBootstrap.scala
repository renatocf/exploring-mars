import org.scalatra.LifeCycle
import javax.servlet.ServletContext

import ml.renatocf.exploringmars.data.DatabaseInit
import ml.renatocf.exploringmars.controllers.MapsController
import ml.renatocf.exploringmars.controllers.ProbesController
import ml.renatocf.exploringmars.swagger.ExploringMarsSwagger
import ml.renatocf.exploringmars.swagger.ExploringMarsApiDocs

class ScalatraBootstrap extends LifeCycle with DatabaseInit {
  implicit val swagger = new ExploringMarsSwagger

  override def init(context: ServletContext) {
    context.initParameters("org.scalatra.cors.allowedOrigins") = "*"
    context.initParameters("org.scalatra.cors.allowCredentials") = "false"

    configureDb()

    context.mount(new ExploringMarsApiDocs, "/")
    context.mount(new MapsController, "/maps", "maps")
    context.mount(new ProbesController, "/probes", "probes")
  }

  override def destroy(context:ServletContext) {
    closeDbConnection()
  }
}
