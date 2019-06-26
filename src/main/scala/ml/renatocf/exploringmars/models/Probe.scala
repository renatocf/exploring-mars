package ml.renatocf.exploringmars.models

import ml.renatocf.exploringmars.models.Direction._
import ml.renatocf.exploringmars.models.Command._

class Probe(map: Map, val position: Point = Point(0, 0), val direction: Direction = NORTH) {
  require(map.checkIfPointIsInsideMap(position))

  def execute(command: Command): Probe = {
    command match {
      case TURN_LEFT => turnLeft
      case TURN_RIGHT => turnRight
      case MOVE => move
    }
  }

  private def turnLeft: Probe = {
    val newDirection = Direction.apply((direction.id+3) % Direction.maxId)
    new Probe(map, position, newDirection)
  }

  private def turnRight: Probe = {
    val newDirection = Direction.apply((direction.id+1) % Direction.maxId)
    new Probe(map, position, newDirection)
  }

  private def move: Probe = {
    val possiblePosition = direction match {
      case NORTH => Point(position.x,   position.y+1)
      case EAST  => Point(position.x+1, position.y  )
      case SOUTH => Point(position.x,   position.y-1)
      case WEST  => Point(position.x-1, position.y  )
    }

    if (map.checkIfPointIsInsideMap(possiblePosition))
    	return new Probe(map, possiblePosition, direction)

		this
  }
}
