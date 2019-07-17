package ml.renatocf.exploringmars.models

import java.util.UUID

import com.typesafe.scalalogging.LazyLogging

import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.StatefulManyToOne

import ml.renatocf.exploringmars.data.MarsDb

import ml.renatocf.exploringmars.models.Direction._
import ml.renatocf.exploringmars.models.Command._

class Probe(
  val id: UUID,
  val mapId: UUID,
  x: Int,
  y: Int,
  val direction: Direction
) extends KeyedEntity[UUID] with LazyLogging {
  lazy val map: StatefulManyToOne[Map] = MarsDb.mapToProbes.rightStateful(this)

  def position: Point = {
    Point(x, y)
  }

  def execute(command: Command): Probe = {
    command match {
      case TURN_LEFT => turnLeft
      case TURN_RIGHT => turnRight
      case MOVE => move
    }
  }

  private def turnLeft: Probe = {
    val newDirection = Direction.apply((direction.id+3) % Direction.maxId)
    new Probe(id, mapId, x, y, newDirection)
  }

  private def turnRight: Probe = {
    val newDirection = Direction.apply((direction.id+1) % Direction.maxId)
    new Probe(id, mapId, x, y, newDirection)
  }

  private def move: Probe = {
    val possiblePosition = direction match {
      case NORTH => Point(x,   y+1)
      case EAST  => Point(x+1, y  )
      case SOUTH => Point(x,   y-1)
      case WEST  => Point(x-1, y  )
    }

    if (checkIfPointIsInsideMap(possiblePosition))
      return new Probe(id, mapId, possiblePosition.x, possiblePosition.y, direction)

    this
  }

  private def checkIfPointIsInsideMap(position: Point) = {
    inTransaction {
      map.one.get.checkIfPointIsInsideMap(position)
    }
  }
}

object Probe {
  def apply(map: Map,
            position: Point = Point(0, 0),
            direction: Direction = NORTH) = {
    require(map.checkIfPointIsInsideMap(position))
    new Probe(UUID.randomUUID(), map.id, position.x, position.y, direction)
  }
}
