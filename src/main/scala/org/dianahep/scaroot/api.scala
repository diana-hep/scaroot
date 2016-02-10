package org.dianahep.scaroot

import scala.language.experimental.macros 
import scala.reflect.macros.blackbox.Context

package api {
  class RootApiException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message, cause.getOrElse(null))

  object FieldType extends Enumeration {
    type FieldType = Value
    val Byte, Short, Int, Long, Float, Double, String = Value
  }

  trait RootTTreeRowBuilder[CASE] {
    def build[ID](rootTTree: RootTTreeReader[CASE, ID], row: Long): CASE
    def leafIdentifiers: Array[Any]
    def nameTypes: Seq[(String, FieldType.Value)]
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
            (q"rootTTree.getValueLeafB", q"FieldType.Byte")
          else if (leafType =:= typeOf[Short])
            (q"rootTTree.getValueLeafS", q"FieldType.Short")
          else if (leafType =:= typeOf[Int])
            (q"rootTTree.getValueLeafI", q"FieldType.Int")
          else if (leafType =:= typeOf[Long])
            (q"rootTTree.getValueLeafL", q"FieldType.Long")
          else if (leafType =:= typeOf[Float])
            (q"rootTTree.getValueLeafF", q"FieldType.Float")
          else if (leafType =:= typeOf[Double])
            (q"rootTTree.getValueLeafD", q"FieldType.Double")
          else if (leafType =:= typeOf[String])
            (q"rootTTree.getValueLeafC", q"FieldType.String")
          else
            throw new NotImplementedError(s"no handler for type $leafType")

        (q"$leafMethod(leafIdentifiers($index).asInstanceOf[ID], row)", q"$leafName -> $t")
      }.unzip

      c.Expr[RootTTreeRowBuilder[CASE]](q"""
        new RootTTreeRowBuilder[$caseType] {
          def build[ID](rootTTree: RootTTreeReader[$caseType, ID], row: Long): $caseType = {
            rootTTree.getRow(row)
            new $caseType(..$buildParams)
          }
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

    def get(row: Long): CASE = rowBuilder.build(this, row)

    def getRow(row: Long): Unit
    def getValueLeafB(leaf: ID, row: Long): Byte
    def getValueLeafS(leaf: ID, row: Long): Short
    def getValueLeafI(leaf: ID, row: Long): Int
    def getValueLeafL(leaf: ID, row: Long): Long
    def getValueLeafF(leaf: ID, row: Long): Float
    def getValueLeafD(leaf: ID, row: Long): Double
    def getValueLeafC(leaf: ID, row: Long): String
  }
}
