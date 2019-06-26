package ml.renatocf.exploringmars.models

object Command extends Enumeration {
  type Command = Value
  val MOVE, TURN_LEFT, TURN_RIGHT = Value
}
