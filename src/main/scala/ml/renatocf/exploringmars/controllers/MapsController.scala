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

class MapsController(implicit val swagger: Swagger)
  extends ScalatraServlet
     with JacksonJsonSupport
     with LazyLogging
     with SwaggerSupport
     with DatabaseSessionSupport {
  protected implicit val jsonFormats = DefaultFormats + new EnumNameSerializer(Direction) + new EnumNameSerializer(Command) ++ JavaTypesSerializers.all
  protected val applicationDescription = "The Maps API. It exposes operations for Maps."

  val baseUrl = System.getenv("BASE_URL")

  before() {
    contentType = formats("json")
  }

  get("/") {
    inTransaction {
      Ok(from(MarsDb.maps)(map => select(map)).toList)
    }
  }

  post("/") {
    try {
      inTransaction {
        val newMap = Map(parsedBody.extract[MapInput])
        MarsDb.maps.insert(newMap)

        val headers = scala.collection.immutable.Map(
          "Location" -> new URL(s"${baseUrl}/maps/${newMap.id}").toString())

        Created(newMap, headers=headers)
      }
    } catch {
      case e: MappingException => BadRequest("Invalid input JSON")
      case unknown: Throwable => InternalServerError()
    }
  }

  get("/:mapId") {
    val mapId = UUID.fromString(params("mapId"))

    try {
      inTransaction {
        val map = MarsDb.maps.get(mapId)
        Ok(map)
      }
    } catch {
      case e: NoSuchElementException => NotFound("Map not found")
      case unknown: Throwable => InternalServerError()
    }
  }

  put("/:mapId") {
    val mapId = UUID.fromString(params("mapId"))

    try {
      inTransaction {
        val map = MarsDb.maps.get(mapId)
        val mapInput = parsedBody.extract[MapInput]
        val updatedMap = new Map(map.id, mapInput.rightmostX, mapInput.upperY)

        MarsDb.maps.update(updatedMap)
        Ok(updatedMap)
      }
    }
    catch {
      case e: NoSuchElementException => NotFound("Map not found")
      case e: MappingException => BadRequest("Invalid input JSON")
      case unknown: Throwable => InternalServerError()
    }
  }

  delete("/:mapId") {
    val mapId = UUID.fromString(params("mapId"))

    inTransaction {
      MarsDb.maps.delete(mapId) match {
        case true => Ok()
        case false => NotFound("Map does not exist")
      }
    }
  }

  get("/:mapId/probes") {
    val mapId = UUID.fromString(params("mapId"))

    try {
      inTransaction {
        val map = MarsDb.maps.get(mapId)
        Ok(map.probes.toList)
      }
    }
    catch {
      case e: NoSuchElementException => NotFound("Map not found")
      case unknown: Throwable => InternalServerError()
    }
  }

  post("/:mapId/probes") {
    val mapId = UUID.fromString(params("mapId"))

    try {
      inTransaction {
        val map = MarsDb.maps.get(mapId)
        val newProbe = Probe(map, parsedBody.extract[ProbeInput])

        map.probes.associate(newProbe)

        val headers = scala.collection.immutable.Map(
          "Location" -> new URL(s"${baseUrl}/maps/${mapId}/probes/${newProbe.id}").toString())

        Created(newProbe, headers=headers)
      }
    }
    catch {
      case e: NoSuchElementException => NotFound("Map not found")
      case e: MappingException => BadRequest("Invalid input JSON")
      case unknown: Throwable => InternalServerError()
    }
  }

  get("/:mapId/probes/:probeId") {
    val mapId = UUID.fromString(params("mapId"))
    val probeId = UUID.fromString(params("probeId"))

    try {
      inTransaction {
        val map = MarsDb.maps.get(mapId)
        val probe = map.probes.find(probe => probe.id == probeId).get

        Ok(probe)
      }
    }
    catch {
      case e: NoSuchElementException => NotFound("Probe or map not found")
      case unknown: Throwable => InternalServerError()
    }
  }

  delete("/:mapId/probes/:probeId") {
    val mapId = UUID.fromString(params("mapId"))
    val probeId = UUID.fromString(params("probeId"))

    try {
      inTransaction {
        val map = MarsDb.maps.get(mapId)
        val probe = map.probes.find(probe => probe.id == probeId).get

        MarsDb.probes.delete(probe.id) match {
          case true => Ok()
          case false => NotFound("Probe not found in the map")
        }
      }
    }
    catch {
      case e: NoSuchElementException => NotFound("Probe or map not found")
      case unknown: Throwable => InternalServerError()
    }
  }

  post("/:mapId/probes/:probeId/execute") {
    val mapId = UUID.fromString(params("mapId"))
    val probeId = UUID.fromString(params("probeId"))

    try {
      inTransaction {
        val commands = parsedBody.extract[List[Command]]

        val map = MarsDb.maps.get(mapId)
        var probe = map.probes.find(probe => probe.id == probeId).get

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
      case e: NoSuchElementException => NotFound("Probe or map not found")
      case e: MappingException => BadRequest("Invalid input JSON")
      case unknown: Throwable => InternalServerError()
    }
  }
}
