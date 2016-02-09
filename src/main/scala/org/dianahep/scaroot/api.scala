package org.dianahep.scaroot

import scala.language.experimental.macros 
import scala.reflect.macros.blackbox.Context

package api {
  trait RootTTreeRowBuilder[T] {
    def build(rootTTree: RootTTreeReader[T], row: Int): T
  }
  object RootTTreeRowBuilder {
    implicit def compileRootTTreeRowBuilder[T]: RootTTreeRowBuilder[T] = macro compileRootTTreeRowBuilderImpl[T]

    def compileRootTTreeRowBuilderImpl[T: c.WeakTypeTag](c: Context): c.Expr[RootTTreeRowBuilder[T]] = {
      import c.universe._
      val tpe = weakTypeOf[T]

      val fields = tpe.decls.collectFirst {
        case m: MethodSymbol if (m.isPrimaryConstructor) => m
      }.get.paramLists.head

      val params = fields.map {field =>
        val name = field.asTerm.name
        val leafName = name.decodedName.toString
        val returnType = tpe.decl(name).typeSignature

        q"rootTTree.getLeafDValue($leafName, row)"
      }

      c.Expr[RootTTreeRowBuilder[T]](q"""
        new RootTTreeRowBuilder[$tpe] {
          def build(rootTTree: RootTTreeReader[$tpe], row: Int): $tpe = new $tpe(..$params)
        }
      """)
    }
  }

  class RootTTreeReader[T](val rootFileLocation: String, val ttreeLocation: String, rowBuilder: RootTTreeRowBuilder[T]) {
    def getLeafDValue(leafName: String, row: Int): Double = 3.14
    def getLeafCValue(leafName: String, row: Int): String = "hello"

    // TLeafB, TLeafC, TLeafD, TLeafElement, TLeafF, TLeafI, TLeafObject, TLeafS

    def get(row: Int): T = rowBuilder.build(this, row)
  }
  object RootTTreeReader {
    def apply[T : RootTTreeRowBuilder](rootFileLocation: String, ttreeLocation: String) =
      new RootTTreeReader(rootFileLocation, ttreeLocation, implicitly[RootTTreeRowBuilder[T]])
  }
}
