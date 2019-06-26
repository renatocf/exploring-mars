package ml.renatocf.exploringmars.models

class Map(val rightmostX: Int, val upperY: Int) {
  require(rightmostX >= 0, "the rightmost point should have non-negative x.")
  require(upperY >= 0, "the upper point should have non-negative y.")

  def checkIfPointIsInsideMap(point: Point) = {
    point.x >= 0 && point.y >= 0 && point.x <= rightmostX && point.y <= upperY
  }
}
