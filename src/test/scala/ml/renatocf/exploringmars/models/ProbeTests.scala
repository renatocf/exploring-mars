package ml.renatocf.exploringmars.models

import org.scalatra.test.scalatest._

class ProbeTests extends ScalatraFlatSpec {
  trait SimpleMap {
    val map = new Map(6, 4)
  }

  "A Probe" should "be initialized within a Map in the origin" in new SimpleMap {
    val probe = new Probe(map)
    probe.position shouldBe Point(0, 0)
    probe.direction shouldBe Direction.NORTH
  }

  it should "be initialized within a Map and non-default x" in new SimpleMap {
    val probe = new Probe(map, position = Point(x = 2))
    probe.position.x shouldBe 2
  }

  it should "be initialized within a Map and non-default y" in new SimpleMap {
    val probe = new Probe(map, position = Point(y = 2))
    probe.position.y shouldBe 2
  }

  it should "be initialized within a Map and non-default direction" in new SimpleMap {
    val probe = new Probe(map, direction = Direction.SOUTH)
    probe.direction shouldBe Direction.SOUTH
  }

  it should "not be initialized outside a Map" in new SimpleMap {
    assertThrows[IllegalArgumentException] {
      val probe = new Probe(map, position = Point(-1, -1))
    }
  }

  trait MapWithProbeInTheCenter {
    val map = new Map(6, 4)
    val probe = new Probe(map, Point(3, 2), Direction.NORTH)
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
    val map = new Map(0, 0)
    val probe = new Probe(map, Point(0, 0), Direction.NORTH)
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
