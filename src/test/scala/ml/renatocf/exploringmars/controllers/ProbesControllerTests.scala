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
import ml.renatocf.exploringmars.models.Point
import ml.renatocf.exploringmars.models.Probe

import ml.renatocf.exploringmars.data.MarsDb
import ml.renatocf.exploringmars.data.DatabaseInit
import ml.renatocf.exploringmars.swagger.ExploringMarsSwagger

class ProbesControllerTests extends ScalatraFlatSpec with DatabaseInit with BeforeAndAfter with LazyLogging {
  implicit val swagger = new ExploringMarsSwagger
  implicit val formats = DefaultFormats + new EnumNameSerializer(Command) + new EnumNameSerializer(Direction) ++ JavaTypesSerializers.all
  addServlet(new ProbesController, "/probes/*")

  configureDb()

  before {
    transaction {
      MarsDb.drop
      MarsDb.create
    }
  }

  after {
    transaction {
      MarsDb.drop
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

  "The ProbesController with one probe" should "return all probes" in new MarsWithOneMapWithOneProbe {
    get(s"/probes") {
      status shouldBe 200
      read[List[Probe]](body).head shouldBe probe
    }
  }

  it should "get the existing probe by its id" in new MarsWithOneMapWithOneProbe {
    get(s"/probes/${probe.id}") {
      status shouldBe 200
      read[Probe](body) shouldBe probe
    }
  }

  it should "not get a nonexisting probe" in new MarsWithOneMapWithOneProbe {
    val nonexistingProbeId = new UUID(0, 0)
    get(s"/probes/${nonexistingProbeId}") {
      status shouldBe 404
      body shouldBe "Probe not found"
    }
  }

  it should "delete the existing probe by its id" in new MarsWithOneMapWithOneProbe {
    delete(s"/probes/${probe.id}") {
      status shouldBe 200
      inTransaction {
        from(MarsDb.probes)(probe => compute(count)).toInt shouldBe 0
      }
    }
  }

  it should "not delete a nonexisting probe" in new MarsWithOneMapWithOneProbe {
    val nonexistingProbeId = new UUID(0, 0)
    delete(s"/probes/${nonexistingProbeId}") {
      status shouldBe 404
      body shouldBe "Probe does not exist"
    }
  }

  it should "delete the existing probe by its id and then not find it in the second try" in new MarsWithOneMapWithOneProbe {
    delete(s"/probes/${probe.id}") {
      status shouldBe 200
      inTransaction {
        from(MarsDb.probes)(probe => compute(count)).toInt shouldBe 0
      }
    }

    delete(s"/probes/${probe.id}") {
      status shouldBe 404
      body shouldBe "Probe does not exist"
    }
  }

  it should "make a probe move" in new MarsWithOneMapWithOneProbe {
    val commands = List(Command.MOVE)
    post(s"/probes/${probe.id}/execute", write(commands).getBytes("utf-8")) {
      status shouldBe 200

      inTransaction {
        val updatedProbe = MarsDb.probes.get(probe.id)
        updatedProbe.id shouldBe probe.id
        updatedProbe.position shouldBe Point(3, 3)
        updatedProbe.direction shouldBe Direction.NORTH
      }
    }
  }

  it should "make a probe turn left" in new MarsWithOneMapWithOneProbe {
    val commands = List(Command.TURN_LEFT)
    post(s"/probes/${probe.id}/execute", write(commands).getBytes("utf-8")) {
      status shouldBe 200

      inTransaction {
        val updatedProbe = MarsDb.probes.get(probe.id)
        updatedProbe.id shouldBe probe.id
        updatedProbe.position shouldBe Point(3, 2)
        updatedProbe.direction shouldBe Direction.WEST
      }
    }
  }

  it should "make a probe turn right" in new MarsWithOneMapWithOneProbe {
    val commands = List(Command.TURN_RIGHT)
    post(s"/probes/${probe.id}/execute", write(commands).getBytes("utf-8")) {
      status shouldBe 200

      inTransaction {
        val updatedProbe = MarsDb.probes.get(probe.id)
        updatedProbe.id shouldBe probe.id
        updatedProbe.position shouldBe Point(3, 2)
        updatedProbe.direction shouldBe Direction.EAST
      }
    }
  }
}
