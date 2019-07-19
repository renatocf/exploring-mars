
package ml.renatocf.exploringmars.models

object Command extends Enumeration {
  type Command = Value
  val MOVE       = Value(0, "MOVE")
  val TURN_LEFT  = Value(1, "TURN_LEFT")
  val TURN_RIGHT = Value(2, "TURN_RIGHT")
}
