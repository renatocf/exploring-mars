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
  "org.json4s"   %% "json4s-jackson" % "3.6.7",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.squeryl" %% "squeryl" % "0.9.14",
  "org.postgresql" % "postgresql" % "42.2.6",
  "com.mchange" % "c3p0" % "0.9.5.4",
  "org.scalatra" %% "scalatra-swagger" % ScalatraVersion
)

logBuffered in Test := false
parallelExecution in Test := false

containerPort in Jetty := sys.env.getOrElse("PORT", "3000").toInt

containerLibs in Jetty := Seq(
  "org.eclipse.jetty" % "jetty-runner" % "9.4.19.v20190610" intransitive()
)

enablePlugins(ScalatraPlugin)

mainClass in assembly := Some("ml.renatocf.exploringmars.JettyLauncher")
