val ScalatraVersion = "2.6.5"

organization := "ml.renatocf"

name := "ExploringMars"

version := "0.1.0"

scalaVersion := "2.12.7"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.19.v20190610" % "compile",
  "javax.servlet" % "javax.servlet-api" % "4.0.1" % "provided",
  "org.scalatra" %% "scalatra-json" % ScalatraVersion,
  "org.json4s"   %% "json4s-jackson" % "3.6.7"
)

enablePlugins(ScalatraPlugin)

mainClass in assembly := Some("ml.renatocf.exploringmars.JettyLauncher")
