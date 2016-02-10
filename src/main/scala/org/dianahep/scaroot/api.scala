package org.dianahep.scaroot

import scala.language.experimental.macros 
import scala.reflect.macros.blackbox.Context

package object api {
  type Identifier = Long   // may be a pointer on the backend
}

package api {
  trait RootTTreeRowBuilder[T] {
    trait LeafType
    case object LeafDouble extends LeafType
    case object LeafString extends LeafType

    def build(rootTTree: RootTTreeReader[T], row: Int): T
    def leafIdentifiers: Array[Identifier]
    def nameTypes: Seq[(String, LeafType)]
  }
  object RootTTreeRowBuilder {
    implicit def compileRootTTreeRowBuilder[T]: RootTTreeRowBuilder[T] = macro compileRootTTreeRowBuilderImpl[T]

    def compileRootTTreeRowBuilderImpl[T: c.WeakTypeTag](c: Context): c.Expr[RootTTreeRowBuilder[T]] = {
      import c.universe._
      val tpe = weakTypeOf[T]

      val fields = tpe.decls.collectFirst {
        case m: MethodSymbol if (m.isPrimaryConstructor) => m
      }.get.paramLists.head

      val (buildParams, nameTypes) = fields.zipWithIndex.map {case (field, index) =>
        val name = field.asTerm.name
        val leafName = name.decodedName.toString
        val NullaryMethodType(leafType) = tpe.decl(name).typeSignature

        val (leafMethod, t) =
          if (leafType =:= typeOf[Double])
            (q"rootTTree.getLeafDValue", q"LeafDouble")
          else if (leafType =:= typeOf[String])
            (q"rootTTree.getLeafCValue", q"LeafString")
          else
            throw new NotImplementedError(s"no handler for type $leafType")

        (q"$leafMethod(leafIdentifiers($index), row)", q"$leafName -> $t")
      }.unzip

      c.Expr[RootTTreeRowBuilder[T]](q"""
        new RootTTreeRowBuilder[$tpe] {
          def build(rootTTree: RootTTreeReader[$tpe], row: Int): $tpe = new $tpe(..$buildParams)
          val leafIdentifiers = Array.fill(${fields.size})(0L)
          val nameTypes = Vector(..$nameTypes)
        }
      """)
    }
  }

  class RootTTreeReader[T](val rootFileLocation: String, val ttreeLocation: String, rowBuilder: RootTTreeRowBuilder[T]) {
    println(rowBuilder.nameTypes)

    def getLeafDValue(leaf: Identifier, row: Int): Double = 3.14
    def getLeafCValue(leaf: Identifier, row: Int): String = "hello"

    // TLeafB, TLeafC, TLeafD, TLeafF, TLeafI, TLeafS

    def get(row: Int): T = rowBuilder.build(this, row)
  }
  object RootTTreeReader {
    def apply[T : RootTTreeRowBuilder](rootFileLocation: String, ttreeLocation: String) =
      new RootTTreeReader(rootFileLocation, ttreeLocation, implicitly[RootTTreeRowBuilder[T]])
  }
}
