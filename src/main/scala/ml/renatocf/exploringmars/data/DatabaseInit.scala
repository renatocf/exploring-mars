package ml.renatocf.exploringmars.data

import com.mchange.v2.c3p0.ComboPooledDataSource

import org.squeryl.adapters.PostgreSqlAdapter
import org.squeryl.Session
import org.squeryl.SessionFactory

import com.typesafe.scalalogging.LazyLogging

trait DatabaseInit extends LazyLogging {
  val dbName = System.getenv("POSTGRES_DB")
  val dbHost = System.getenv("POSTGRES_HOST")
  val dbPort = System.getenv("POSTGRES_PORT").toInt
  val dbUsername = System.getenv("POSTGRES_USER")
  val dbPassword = System.getenv("POSTGRES_PASSWORD")

  val dbConnection = s"jdbc:postgresql://${dbHost}:${dbPort}/${dbName}"

  var cpds = new ComboPooledDataSource

  def configureDb() {
    cpds.setDriverClass("org.postgresql.Driver")
    cpds.setJdbcUrl(dbConnection)
    cpds.setUser(dbUsername)
    cpds.setPassword(dbPassword)

    cpds.setMinPoolSize(1)
    cpds.setAcquireIncrement(1)
    cpds.setMaxPoolSize(50)

    SessionFactory.concreteFactory = Some(() => connection)

    def connection = {
      Session.create(cpds.getConnection, new PostgreSqlAdapter)
    }
  }

  def closeDbConnection() {
    cpds.close()
  }
}
