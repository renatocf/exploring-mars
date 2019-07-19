package ml.renatocf.exploringmars.controllers

import java.util.UUID
import java.net.URL

import org.scalatra._
import org.scalatra.json._
import org.scalatra.swagger._

import org.json4s.DefaultFormats
import org.json4s.MappingException
import org.json4s.ext.EnumNameSerializer
import org.json4s.ext.JavaTypesSerializers
import org.json4s.jackson.Serialization

import com.typesafe.scalalogging.LazyLogging

import org.squeryl.PrimitiveTypeMode._

import ml.renatocf.exploringmars.models.Command
import ml.renatocf.exploringmars.models.Command._
import ml.renatocf.exploringmars.models.Direction
import ml.renatocf.exploringmars.models.Map
import ml.renatocf.exploringmars.models.MapInput
import ml.renatocf.exploringmars.models.Probe
import ml.renatocf.exploringmars.models.ProbeInput

import ml.renatocf.exploringmars.data.MarsDb
import ml.renatocf.exploringmars.data.DatabaseSessionSupport

class ProbesController(implicit val swagger: Swagger)
  extends ScalatraServlet
     with JacksonJsonSupport
     with LazyLogging
     with SwaggerSupport
     with DatabaseSessionSupport {
  protected implicit val jsonFormats = DefaultFormats + new EnumNameSerializer(Direction) + new EnumNameSerializer(Command) ++ JavaTypesSerializers.all
  protected val applicationDescription = "The Probes API. It exposes operations for Probes."

  before() {
    contentType = formats("json")
  }

  get("/") {
    inTransaction {
      Ok(from(MarsDb.probes)(probe => select(probe)).toList)
    }
  }

  get("/:probeId") {
    val probeId = UUID.fromString(params("probeId"))

    try {
      inTransaction {
        val probe = MarsDb.probes.get(probeId)
        Ok(probe)
      }
    } catch {
      case e: NoSuchElementException => NotFound("Probe not found")
      case unknown: Throwable => InternalServerError()
    }
  }

  delete("/:probeId") {
    val probeId = UUID.fromString(params("probeId"))

    inTransaction {
      MarsDb.probes.delete(probeId) match {
        case true => Ok()
        case false => NotFound("Probe does not exist")
      }
    }
  }

  post("/:probeId/execute") {
    val probeId = UUID.fromString(params("probeId"))

    try {
      inTransaction {
        val commands = parsedBody.extract[List[Command]]

        var probe = MarsDb.probes.get(probeId)

        logger.info(s"Before:\n${probe}")
        for (command <- commands) {
          probe = probe.execute(command)

          inTransaction {
            MarsDb.probes.update(probe)
          }

          logger.info(s"Executed s${command}:\n${probe}")
        }
        logger.info(s"After:\n${probe}")

        Ok(probe)
      }
    }
    catch {
      case e: NoSuchElementException => NotFound("Probe not found")
      case e: MappingException => BadRequest("Invalid input JSON")
      case unknown: Throwable => InternalServerError()
    }
  }
}

