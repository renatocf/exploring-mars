package ml.renatocf.exploringmars.models

import java.util.UUID

import org.squeryl.KeyedEntity
import org.squeryl.dsl.StatefulOneToMany
import org.squeryl.PrimitiveTypeMode.inTransaction

import ml.renatocf.exploringmars.data.MarsDb

class Map(
  val id: UUID,
  val rightmostX: Int,
  val upperY: Int
) extends KeyedEntity[UUID] {
  lazy val probes: StatefulOneToMany[Probe] = MarsDb.mapToProbes.leftStateful(this)

  require(rightmostX >= 0, "the rightmost point should have non-negative x.")
  require(upperY >= 0, "the upper point should have non-negative y.")

  def checkIfPointIsInsideMap(point: Point) = {
    point.x >= 0 && point.y >= 0 && point.x <= rightmostX && point.y <= upperY
  }
}

object Map {
  def apply(rightmostX: Int = 0, upperY: Int = 0) = {
    new Map(UUID.randomUUID(), rightmostX, upperY)
  }
}
