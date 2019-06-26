package ml.renatocf.exploringmars.models

import org.scalatra.test.scalatest._

class MapTests extends ScalatraFlatSpec {
  "A Map" should "be created with the upper rightmost point coordinates" in {
    assert(new Map(6, 4) != null)
  }

  it should "have a non-negative rightmost point x coordinate" in {
    assertThrows[IllegalArgumentException] {
      new Map(-1, 4)
    }
  }

  it should "have a non-negative upper point y coordinate" in {
    assertThrows[IllegalArgumentException] {
      new Map(6, -1)
    }
  }

  trait SimpleMap {
    val map = new Map(6, 4)
  }

  "A Simple Map" should "verify a point within its limits is inside the map" in new SimpleMap {
    val insidePoint = Point(map.rightmostX-1, map.upperY-1)
    map.checkIfPointIsInsideMap(insidePoint) shouldBe true
  }

  it should "verify that a point with large x coordinate is outside the map" in new SimpleMap {
    val outsidePoint = Point(map.rightmostX-1, map.upperY + 1)
    map.checkIfPointIsInsideMap(outsidePoint) shouldBe false
  }

  it should "verify that a point with large y coordinate is outside the map" in new SimpleMap {
    val outsidePoint = Point(map.rightmostX + 1, map.upperY-1)
    map.checkIfPointIsInsideMap(outsidePoint) shouldBe false
  }

  it should "verify that a point with negative x coordinate is outside the map" in new SimpleMap {
    val outsidePoint = Point(-1, map.upperY-1)
    map.checkIfPointIsInsideMap(outsidePoint) shouldBe false
  }

  it should "verify that a point with negative y coordinate is outside the map" in new SimpleMap {
    val outsidePoint = Point(map.rightmostX-1, -1)
    map.checkIfPointIsInsideMap(outsidePoint) shouldBe false
  }
}
