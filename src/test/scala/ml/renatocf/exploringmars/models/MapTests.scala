package ml.renatocf.exploringmars.models

import com.typesafe.scalalogging.LazyLogging

import org.scalatest.BeforeAndAfter

import org.scalatra.test.scalatest._

import org.squeryl.PrimitiveTypeMode._

import ml.renatocf.exploringmars.data.DatabaseInit
import ml.renatocf.exploringmars.data.MarsDb

class MapTests extends ScalatraFlatSpec with DatabaseInit with BeforeAndAfter with LazyLogging {
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

  "A Map" should "be created with the upper rightmost point coordinates" in {
    assert(Map(6, 4) != null)
  }

  it should "have a non-negative rightmost point x coordinate" in {
    assertThrows[IllegalArgumentException] {
      Map(-1, 4)
    }
  }

  it should "have a non-negative upper point y coordinate" in {
    assertThrows[IllegalArgumentException] {
      Map(6, -1)
    }
  }

  it should "be created from a map input" in {
    val mapInput = MapInput(6, 4)
    assert(Map(mapInput) != null)
  }

  trait SimpleMap {
    val map = Map(6, 4)
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
