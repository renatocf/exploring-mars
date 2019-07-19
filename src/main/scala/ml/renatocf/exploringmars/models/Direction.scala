package ml.renatocf.exploringmars.models

object Direction extends Enumeration {
  type Direction = Value
  val NORTH = Value(0, "NORTH")
  val EAST  = Value(1, "EAST")
  val SOUTH = Value(2, "SOUTH")
  val WEST  = Value(3, "WEST")
}
