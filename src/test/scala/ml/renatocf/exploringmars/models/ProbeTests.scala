package ml.renatocf.exploringmars.models

import java.util.UUID

import com.typesafe.scalalogging.LazyLogging

import org.scalatest.BeforeAndAfter

import org.scalatra.test.scalatest._

import org.squeryl.PrimitiveTypeMode._

import ml.renatocf.exploringmars.data.DatabaseInit
import ml.renatocf.exploringmars.data.MarsDb

class ProbeTests extends ScalatraFlatSpec with DatabaseInit with BeforeAndAfter with LazyLogging {
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

  trait SimpleMap {
    val map = Map(6, 4)
  }

  "A Probe" should "be initialized within a Map in the origin" in new SimpleMap {
    val probe = Probe(map)
    probe.position shouldBe Point(0, 0)
    probe.direction shouldBe Direction.NORTH
  }

  it should "be initialized within a Map and non-default x" in new SimpleMap {
    val probe = Probe(map, position = Point(x = 2))
    probe.position.x shouldBe 2
  }

  it should "be initialized within a Map and non-default y" in new SimpleMap {
    val probe = Probe(map, position = Point(y = 2))
    probe.position.y shouldBe 2
  }

  it should "be initialized within a Map and non-default direction" in new SimpleMap {
    val probe = Probe(map, direction = Direction.SOUTH)
    probe.direction shouldBe Direction.SOUTH
  }

  it should "not be initialized outside a Map" in new SimpleMap {
    assertThrows[IllegalArgumentException] {
      val probe = Probe(map, position = Point(-1, -1))
    }
  }

  it should "be initialized with a valid Probe input" in new SimpleMap {
    val probeInput = ProbeInput(6, 4, Direction.NORTH)
    assert(Probe(map, probeInput) != null)
  }

  trait MapWithProbeInTheCenter {
    val map = Map(6, 4)
    val probe = Probe(map, Point(3, 2), Direction.NORTH)

    inTransaction {
      MarsDb.maps.insert(map)
    }
  }

  "A Probe in the center of a Map pointing North" should "be positioned inside the map" in new MapWithProbeInTheCenter {
    map.checkIfPointIsInsideMap(probe.position) shouldBe true
  }

  it should "turn left once and point West" in new MapWithProbeInTheCenter {
    probe
      .execute(Command.TURN_LEFT)
      .direction shouldBe Direction.WEST
  }

  it should "turn left twice and point South" in new MapWithProbeInTheCenter {
    probe
      .execute(Command.TURN_LEFT)
      .execute(Command.TURN_LEFT)
      .direction shouldBe Direction.SOUTH
  }

  it should "turn left three times and point East" in new MapWithProbeInTheCenter {
    probe
      .execute(Command.TURN_LEFT)
      .execute(Command.TURN_LEFT)
      .execute(Command.TURN_LEFT)
      .direction shouldBe Direction.EAST
  }

  it should "turn left four times and point North" in new MapWithProbeInTheCenter {
    probe
      .execute(Command.TURN_LEFT)
      .execute(Command.TURN_LEFT)
      .execute(Command.TURN_LEFT)
      .execute(Command.TURN_LEFT)
      .direction shouldBe Direction.NORTH
  }

  it should "turn right once and point East" in new MapWithProbeInTheCenter {
    probe
      .execute(Command.TURN_RIGHT)
      .direction shouldBe Direction.EAST
  }

  it should "turn right twice and point South" in new MapWithProbeInTheCenter {
    probe
      .execute(Command.TURN_RIGHT)
      .execute(Command.TURN_RIGHT)
      .direction shouldBe Direction.SOUTH
  }

  it should "turn right three times and point West" in new MapWithProbeInTheCenter {
    probe
      .execute(Command.TURN_RIGHT)
      .execute(Command.TURN_RIGHT)
      .execute(Command.TURN_RIGHT)
      .direction shouldBe Direction.WEST
  }

  it should "turn right four times and point North" in new MapWithProbeInTheCenter {
    probe
      .execute(Command.TURN_RIGHT)
      .execute(Command.TURN_RIGHT)
      .execute(Command.TURN_RIGHT)
      .execute(Command.TURN_RIGHT)
      .direction shouldBe Direction.NORTH
  }

  it should "stay in its direction and move North" in new MapWithProbeInTheCenter {
    probe
      .execute(Command.MOVE)
      .position shouldBe Point(3, 3)
  }

  it should "turn right and move East" in new MapWithProbeInTheCenter {
    probe
      .execute(Command.TURN_RIGHT)
      .execute(Command.MOVE)
      .position shouldBe Point(4, 2)
  }

  it should "turn left and move West" in new MapWithProbeInTheCenter {
    probe
      .execute(Command.TURN_LEFT)
      .execute(Command.MOVE)
      .position shouldBe Point(2, 2)
  }

  it should "turn around and move South" in new MapWithProbeInTheCenter {
    probe
      .execute(Command.TURN_RIGHT)
      .execute(Command.TURN_RIGHT)
      .execute(Command.MOVE)
      .position shouldBe Point(3, 1)
  }

  trait SmallestMapWithProbe {
    val map = Map(0, 0)
    val probe = Probe(map, Point(0, 0), Direction.NORTH)

    inTransaction {
      MarsDb.maps.insert(map)
    }
  }

  "A Probe in the smallest map pointing North" should "be positioned inside the map" in new SmallestMapWithProbe {
    map.checkIfPointIsInsideMap(probe.position) shouldBe true
  }

  it should "not be able to move North" in new SmallestMapWithProbe {
    probe
      .execute(Command.MOVE)
      .position shouldBe Point(0, 0)
  }

  it should "not be able to turn right and move East" in new SmallestMapWithProbe {
    probe
      .execute(Command.TURN_RIGHT)
      .execute(Command.MOVE)
      .position shouldBe Point(0, 0)
  }

  it should "not be able to turn left and move West" in new SmallestMapWithProbe {
    probe
      .execute(Command.TURN_LEFT)
      .execute(Command.MOVE)
      .position shouldBe Point(0, 0)
  }

  it should "not be able to turn around and move South" in new SmallestMapWithProbe {
    probe
      .execute(Command.TURN_RIGHT)
      .execute(Command.TURN_RIGHT)
      .execute(Command.MOVE)
      .position shouldBe Point(0, 0)
  }
}
