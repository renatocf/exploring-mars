package ml.renatocf.exploringmars.data

import org.squeryl.Session
import org.squeryl.SessionFactory
import org.scalatra._

object DatabaseSessionSupport {
  val key = {
    val name = getClass.getName
    if (name.endsWith("$")) name.dropRight(1) else name
  }
}

trait DatabaseSessionSupport { this: ScalatraBase =>
  import DatabaseSessionSupport._

  def dbSession = request.get(key).orNull.asInstanceOf[Session]

  before() {
    request(key) = SessionFactory.newSession
    dbSession.bindToCurrentThread
  }

  after() {
    dbSession.close
    dbSession.unbindFromCurrentThread
  }
}
