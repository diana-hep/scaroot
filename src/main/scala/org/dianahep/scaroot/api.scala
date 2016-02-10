package org.dianahep.scaroot

import scala.language.experimental.macros 
import scala.reflect.macros.blackbox.Context

package api {
  trait RootTTreeRowBuilder[CASE] {
    trait LeafType
    case object LeafByte extends LeafType
    case object LeafShort extends LeafType
    case object LeafInt extends LeafType
    case object LeafLong extends LeafType
    case object LeafFloat extends LeafType
    case object LeafDouble extends LeafType
    case object LeafString extends LeafType

    def build[ID](rootTTree: RootTTreeReader[CASE, ID], row: Int): CASE
    def leafIdentifiers: Array[Any]
    def nameTypes: Seq[(String, LeafType)]
  }
  object RootTTreeRowBuilder {
    implicit def compileRootTTreeRowBuilder[CASE]: RootTTreeRowBuilder[CASE] = macro compileRootTTreeRowBuilderImpl[CASE]

    def compileRootTTreeRowBuilderImpl[CASE : c.WeakTypeTag](c: Context): c.Expr[RootTTreeRowBuilder[CASE]] = {
      import c.universe._
      val caseType = weakTypeOf[CASE]

      val fields = caseType.decls.collectFirst {
        case m: MethodSymbol if (m.isPrimaryConstructor) => m
      }.get.paramLists.head

      val (buildParams, nameTypes) = fields.zipWithIndex.map {case (field, index) =>
        val name = field.asTerm.name
        val leafName = name.decodedName.toString
        val NullaryMethodType(leafType) = caseType.decl(name).typeSignature

        val (leafMethod, t) =
          if (leafType =:= typeOf[Byte])
            (q"rootTTree.getValueLeafB", q"LeafByte")
          else if (leafType =:= typeOf[Short])
            (q"rootTTree.getValueLeafS", q"LeafShort")
          else if (leafType =:= typeOf[Int])
            (q"rootTTree.getValueLeafI", q"LeafInt")
          else if (leafType =:= typeOf[Long])
            (q"rootTTree.getValueLeafL", q"LeafLong")
          else if (leafType =:= typeOf[Float])
            (q"rootTTree.getValueLeafF", q"LeafFloat")
          else if (leafType =:= typeOf[Double])
            (q"rootTTree.getValueLeafD", q"LeafDouble")
          else if (leafType =:= typeOf[String])
            (q"rootTTree.getValueLeafC", q"LeafString")
          else
            throw new NotImplementedError(s"no handler for type $leafType")

        (q"$leafMethod(leafIdentifiers($index).asInstanceOf[ID], row)", q"$leafName -> $t")
      }.unzip

      c.Expr[RootTTreeRowBuilder[CASE]](q"""
        new RootTTreeRowBuilder[$caseType] {
          def build[ID](rootTTree: RootTTreeReader[$caseType, ID], row: Int): $caseType = new $caseType(..$buildParams)
          val leafIdentifiers = Array.fill(${fields.size})(null.asInstanceOf[Any])
          val nameTypes = Vector(..$nameTypes)
        }
      """)
    }
  }

  abstract class RootTTreeReader[CASE, ID](rowBuilder: RootTTreeRowBuilder[CASE]) {
    def rootFileLocation: String
    def ttreeLocation: String
    def size: Long

    def get(row: Int): CASE = rowBuilder.build(this, row)

    def getValueLeafB(leaf: ID, row: Int): Byte
    def getValueLeafS(leaf: ID, row: Int): Short
    def getValueLeafI(leaf: ID, row: Int): Int
    def getValueLeafL(leaf: ID, row: Int): Long
    def getValueLeafF(leaf: ID, row: Int): Float
    def getValueLeafD(leaf: ID, row: Int): Double
    def getValueLeafC(leaf: ID, row: Int): String
  }
}
