package org.dianahep

import scala.language.experimental.macros 
import scala.reflect.macros.blackbox.Context

import com.sun.jna._

package object scaroot {
  trait RootTTreeRowBuilder[T] {
    def build(rootTTree: RootTTree[T], row: Int): T
  }
  object RootTTreeRowBuilder {
    implicit def compileRootTTreeRowBuilder[T]: RootTTreeRowBuilder[T] = macro compileRootTTreeRowBuilderImpl[T]

    def compileRootTTreeRowBuilderImpl[T: c.WeakTypeTag](c: Context): c.Expr[RootTTreeRowBuilder[T]] = {
      import c.universe._
      val tpe = weakTypeOf[T]
      val companion = tpe.typeSymbol.companion

      val fields = tpe.decls.collectFirst {
        case m: MethodSymbol if (m.isPrimaryConstructor) => m
      }.get.paramLists.head

      val params = fields.map { field =>
        val name = field.asTerm.name
        val leafName = name.decodedName.toString
        val returnType = tpe.decl(name).typeSignature

        q"rootTTree.getLeafD($leafName, row)"
      }

      c.Expr[RootTTreeRowBuilder[T]](q"""
        new RootTTreeRowBuilder[$tpe] {
          def build(rootTTree: RootTTree[$tpe], row: Int): $tpe = $companion(..$params)
        }
      """)
    }
  }
}

package scaroot {
  class RootTTree[T](val rootFileLocation: String, val ttreeLocation: String, rowBuilder: RootTTreeRowBuilder[T]) {
    def getLeafD(leafName: String, row: Int): Double = 3.14
    def getLeafC(leafName: String, row: Int): String = "hello"

    def get(row: Int): T = rowBuilder.build(this, row)
  }
  object RootTTree {
    def apply[T: RootTTreeRowBuilder](rootFileLocation: String, ttreeLocation: String) =
      new RootTTree(rootFileLocation, ttreeLocation, implicitly[RootTTreeRowBuilder[T]])
  }

  object SharedObject extends Library {
    Native.register("/resources/native/scaroot.so")
    @native def new_TFile(fileName: String): Long
    @native def delete_TFile(pointer: Long): Unit
    @native def TFile_ls(pointer: Long): Unit
  }

  object Main {
    def main(args: Array[String]) {
      val pointer = SharedObject.new_TFile("/opt/root/test/Event.root")
      println(s"pointer value $pointer")
      SharedObject.TFile_ls(pointer)
      println(s"see a listing?")
      SharedObject.delete_TFile(pointer)
      println(s"still here?")
    }
  }
}
