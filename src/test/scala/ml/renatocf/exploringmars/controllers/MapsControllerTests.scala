package ml.renatocf.exploringmars.controllers

import java.util.UUID
import java.net.URL

import com.typesafe.scalalogging.LazyLogging

import org.scalatest.BeforeAndAfter

import org.scalatra.test.scalatest._

import org.squeryl.PrimitiveTypeMode._

import org.json4s.DefaultFormats
import org.json4s.ext.EnumNameSerializer
import org.json4s.ext.JavaTypesSerializers
import org.json4s.jackson.Serialization.{read, write}

import ml.renatocf.exploringmars.models.Command
import ml.renatocf.exploringmars.models.Direction
import ml.renatocf.exploringmars.models.Map
import ml.renatocf.exploringmars.models.MapInput
import ml.renatocf.exploringmars.models.Point
import ml.renatocf.exploringmars.models.Probe
import ml.renatocf.exploringmars.models.ProbeInput

import ml.renatocf.exploringmars.data.MarsDb
import ml.renatocf.exploringmars.data.DatabaseInit
import ml.renatocf.exploringmars.swagger.ExploringMarsSwagger

class MapsControllerTests extends ScalatraFlatSpec with DatabaseInit with BeforeAndAfter with LazyLogging {
  implicit val swagger = new ExploringMarsSwagger
  implicit val formats = DefaultFormats + new EnumNameSerializer(Command) + new EnumNameSerializer(Direction) ++ JavaTypesSerializers.all
  addServlet(new MapsController, "/maps/*")

  configureDb()

  before {
    transaction {
      MarsDb.drop
      MarsDb.create
    }
  }

  trait MarsWithOneMapAndNoProbes extends LazyLogging {
    val map = Map(6, 4)

    inTransaction {
      MarsDb.maps.insert(map)
    }
  }

  "The MapsController with a one map and no probe" should "return all its maps" in new MarsWithOneMapAndNoProbes {
    get("/maps") {
      status shouldBe 200
      read[List[Map]](body).head shouldBe map
    }
  }

  it should "register a new map" in new MarsWithOneMapAndNoProbes {
    val mapInput = MapInput(1, 1)
    post("/maps", write(mapInput).getBytes("utf-8")) {
      status shouldBe 201

      response.headers.contains("Location") shouldBe true
      val newMapUrl = new URL(response.headers.get("Location").get.head)
      val newMapUrlPath = newMapUrl.getPath().replaceFirst("/", "")
      val newMapUrlPathComponents = newMapUrlPath.split("/")

      newMapUrlPathComponents(0) shouldBe "maps"
      val newMapId = UUID.fromString(newMapUrlPathComponents(1))

      inTransaction {
        from(MarsDb.maps)(map => compute(count)).toInt shouldBe 2

        val newMap = MarsDb.maps.get(newMapId)
        newMap.id shouldBe newMapId
        newMap.rightmostX shouldBe mapInput.rightmostX
        newMap.upperY shouldBe mapInput.upperY
      }
    }
  }

  it should "not register a new map for an invalid input" in new MarsWithOneMapAndNoProbes {
    val invalidJson = """{"answer": "42"}""""
    post("/maps", invalidJson.getBytes("utf-8")) {
      status shouldBe 400
      body shouldBe "Invalid input JSON"
    }
  }

  it should "get the existing map by its id" in new MarsWithOneMapAndNoProbes {
    get(s"/maps/${map.id}") {
      status shouldBe 200
      read[Map](body) shouldBe map
    }
  }

  it should "not get a nonexisting map" in new MarsWithOneMapAndNoProbes {
    val nonexistingMapId = new UUID(0, 0)
    get(s"/maps/${nonexistingMapId}") {
      status shouldBe 404
      body shouldBe "Map not found"
    }
  }

  it should "update the existing map by its id" in new MarsWithOneMapAndNoProbes {
    val updatedMapInput = new MapInput(7, 5)
    put(s"/maps/${map.id}", write(updatedMapInput).getBytes("utf-8")) {
      status shouldBe 200
      inTransaction {
        val updatedMap = MarsDb.maps.get(map.id)
        updatedMap.rightmostX shouldBe updatedMapInput.rightmostX
        updatedMap.upperY shouldBe updatedMapInput.upperY
      }
    }
  }

  it should "not update a nonexisting map" in new MarsWithOneMapAndNoProbes {
    val nonexistingMap = new Map(new UUID(0, 0), 7, 5)
    put(s"/maps/${nonexistingMap.id}", write(nonexistingMap).getBytes("utf-8")) {
      status shouldBe 404
      body shouldBe "Map not found"
    }
  }

  it should "not update an existing map from an invalid input" in new MarsWithOneMapAndNoProbes {
    val invalidJson = """{"answer": "42"}""""
    put(s"/maps/${map.id}", invalidJson.getBytes("utf-8")) {
      status shouldBe 400
    }
  }

  it should "delete the existing map by its id" in new MarsWithOneMapAndNoProbes {
    delete(s"/maps/${map.id}") {
      status shouldBe 200
      inTransaction {
        from(MarsDb.maps)(map => compute(count)).toInt shouldBe 0
      }
    }
  }

  it should "not delete a nonexisting map" in new MarsWithOneMapAndNoProbes {
    val nonexistingMap = new Map(new UUID(0, 0), 7, 5)
    delete(s"/maps/${nonexistingMap.id}") {
      status shouldBe 404
      body shouldBe "Map does not exist"
    }
  }

  it should "delete the existing map by its id and then not find it in the second try" in new MarsWithOneMapAndNoProbes {
    delete(s"/maps/${map.id}") {
      status shouldBe 200
      inTransaction {
        from(MarsDb.maps)(map => compute(count)).toInt shouldBe 0
      }
    }

    delete(s"/maps/${map.id}") {
      status shouldBe 404
      body shouldBe "Map does not exist"
    }
  }

  trait MarsWithOneMapWithOneProbe extends LazyLogging {
    val map = Map(6, 4)
    val probe = Probe(map, Point(3, 2), Direction.NORTH)

    inTransaction {
      MarsDb.maps.insert(map)
      MarsDb.probes.insert(probe)
    }
  }

  "The MapsController with one map and one probe" should "return all probes in the map" in new MarsWithOneMapWithOneProbe {
    get(s"/maps/${map.id}/probes") {
      status shouldBe 200
      read[List[Probe]](body).head shouldBe probe
    }
  }

  it should "not return all probes for a nonexisting map" in new MarsWithOneMapWithOneProbe {
    val nonexistingMapId = new UUID(0, 0)
    get(s"/maps/${nonexistingMapId}/probes") {
      status shouldBe 404
      body shouldBe "Map not found"
    }
  }

  it should "register a new probe in the map" in new MarsWithOneMapWithOneProbe {
    val probeInput = ProbeInput(0, 0, Direction.NORTH)
    post(s"/maps/${map.id}/probes", write(probeInput).getBytes("utf-8")) {
      status shouldBe 201

      response.headers.contains("Location") shouldBe true
      val newProbeUrl = new URL(response.headers.get("Location").get.head)
      val newProbeUrlPath = newProbeUrl.getPath().replaceFirst("/", "")
      val newProbeUrlPathComponents = newProbeUrlPath.split("/")

      newProbeUrlPathComponents(0) shouldBe "maps"
      val mapId = UUID.fromString(newProbeUrlPathComponents(1))

      newProbeUrlPathComponents(2) shouldBe "probes"
      val newProbeId = UUID.fromString(newProbeUrlPathComponents(3))

      inTransaction {
        MarsDb.maps.get(map.id).probes.count(probe => true) shouldBe 2

        val newProbe = MarsDb.probes.get(newProbeId)
        newProbe.id shouldBe newProbeId
        newProbe.mapId shouldBe mapId
        newProbe.x shouldBe probeInput.x
        newProbe.y shouldBe probeInput.y
        newProbe.direction shouldBe probeInput.direction
      }
    }
  }

  it should "not register a new probe for a nonexisting map" in new MarsWithOneMapWithOneProbe {
    val nonexistingMapId = new UUID(0, 0)
    val probeInput = ProbeInput(0, 0, Direction.NORTH)
    post(s"/maps/${nonexistingMapId}/probes", write(probeInput).getBytes("utf-8")) {
      status shouldBe 404
      body shouldBe "Map not found"
    }
  }

  it should "not register a new probe for an invalid input" in new MarsWithOneMapWithOneProbe {
    val invalidJson = """{"answer": "42"}""""
    post(s"/maps/${map.id}/probes", invalidJson.getBytes("utf-8")) {
      status shouldBe 400
      body shouldBe "Invalid input JSON"
    }
  }

  it should "get the existing probe in the map by its id" in new MarsWithOneMapWithOneProbe {
    get(s"/maps/${map.id}/probes/${probe.id}") {
      status shouldBe 200
      read[Probe](body) shouldBe probe
    }
  }

  it should "not get a probe for a nonexisting map" in new MarsWithOneMapWithOneProbe {
    val nonexistingMapId = new UUID(0, 0)
    get(s"/maps/${nonexistingMapId}/probes/${probe.id}") {
      status shouldBe 404
      body shouldBe "Probe or map not found"
    }
  }

  it should "not get a nonexisting probe in the map" in new MarsWithOneMapWithOneProbe {
    val nonexistingProbeId = new UUID(0, 0)
    get(s"/maps/${map.id}/probes/${nonexistingProbeId}") {
      status shouldBe 404
      body shouldBe "Probe or map not found"
    }
  }

  it should "delete the existing probe in the map by its id" in new MarsWithOneMapWithOneProbe {
    delete(s"/maps/${map.id}/probes/${probe.id}") {
      status shouldBe 200
      inTransaction {
        MarsDb.maps.get(map.id).probes.count(probe => true) shouldBe 0
      }
    }
  }

  it should "not delete a probe in an nonexisting map" in new MarsWithOneMapWithOneProbe {
    val nonexistingMapId = new UUID(0, 0)
    delete(s"/maps/${nonexistingMapId}/probes/${probe.id}") {
      status shouldBe 404
      body shouldBe "Probe or map not found"
    }
  }

  it should "not delete a nonexisting probe in the map" in new MarsWithOneMapWithOneProbe {
    val nonexistingProbeId = new UUID(0, 0)
    delete(s"/maps/${map.id}/probes/${nonexistingProbeId}") {
      status shouldBe 404
      body shouldBe "Probe or map not found"
    }
  }

  it should "delete the existing probe in the map by its id and then not find it in the second try" in new MarsWithOneMapWithOneProbe {
    delete(s"/maps/${map.id}/probes/${probe.id}") {
      status shouldBe 200
      inTransaction {
        MarsDb.maps.get(map.id).probes.count(probe => true) shouldBe 0
      }
    }

    delete(s"/maps/${map.id}/probes/${probe.id}") {
      status shouldBe 404
      body shouldBe "Probe or map not found"
    }
  }

  it should "make a probe move" in new MarsWithOneMapWithOneProbe {
    val commands = List(Command.MOVE)
    post(s"/maps/${map.id}/probes/${probe.id}/execute", write(commands).getBytes("utf-8")) {
      status shouldBe 200

      inTransaction {
        val updatedProbe = map.probes.find(p => p.id == probe.id).get
        updatedProbe.id shouldBe probe.id
        updatedProbe.position shouldBe Point(3, 3)
        updatedProbe.direction shouldBe Direction.NORTH
      }
    }
  }

  it should "make a probe turn left" in new MarsWithOneMapWithOneProbe {
    val commands = List(Command.TURN_LEFT)
    post(s"/maps/${map.id}/probes/${probe.id}/execute", write(commands).getBytes("utf-8")) {
      status shouldBe 200

      inTransaction {
        val updatedProbe = map.probes.find(p => p.id == probe.id).get
        updatedProbe.id shouldBe probe.id
        updatedProbe.position shouldBe Point(3, 2)
        updatedProbe.direction shouldBe Direction.WEST
      }
    }
  }

  it should "make a probe turn right" in new MarsWithOneMapWithOneProbe {
    val commands = List(Command.TURN_RIGHT)
    post(s"/maps/${map.id}/probes/${probe.id}/execute", write(commands).getBytes("utf-8")) {
      status shouldBe 200

      inTransaction {
        val updatedProbe = map.probes.find(p => p.id == probe.id).get
        updatedProbe.id shouldBe probe.id
        updatedProbe.position shouldBe Point(3, 2)
        updatedProbe.direction shouldBe Direction.EAST
      }
    }
  }

  trait MarsWithTwoMapsWithOneProbeEach extends LazyLogging {
    val map1 = Map(6, 4)
    val probe1 = Probe(map1, Point(3, 2), Direction.NORTH)

    val map2 = Map(0, 0)
    val probe2 = Probe(map2, Point(0, 0), Direction.NORTH)

    inTransaction {
      MarsDb.maps.insert(map1)
      MarsDb.probes.insert(probe1)

      MarsDb.maps.insert(map2)
      MarsDb.probes.insert(probe2)
    }
  }

  it should "not find a probe that is not in the map to delete it" in new MarsWithTwoMapsWithOneProbeEach {
    delete(s"/maps/${map1.id}/probes/${probe2.id}") {
      status shouldBe 404
      body shouldBe "Probe or map not found"
    }
  }
}
