package ml.renatocf.exploringmars.models

case class Point(val x: Int = 0, val y: Int = 0) {
  override def toString: String = "(" + x + "," + y + ")"
}
