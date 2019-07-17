package ml.renatocf.exploringmars.data

import org.squeryl.Schema
import org.squeryl.PrimitiveTypeMode._

import ml.renatocf.exploringmars.models.Map
import ml.renatocf.exploringmars.models.Probe

object MarsDb extends Schema {
  val maps = table[Map]

  val probes = table[Probe]

  val mapToProbes =
    oneToManyRelation(maps, probes).
    via((map, probe) => map.id === probe.mapId)
}
