package org.dianahep

import scala.language.experimental.macros
// import scala.reflect.macros.blackbox.Context
import scala.reflect.macros.Context

import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer

package scaroot {
  trait Param {
    def name: String
    def value: Pointer
  }
  case class IntParam(name: String) extends Param {
    val value = new Memory(Native.getNativeSize(java.lang.Integer.TYPE))
    def apply(x: Int) = {
      value.setInt(0, x)
      value
    }
  }

  trait Ret
  case class IntRet() extends Ret {
    val value = new Memory(Native.getNativeSize(java.lang.Integer.TYPE))
    def apply() = value.getInt(0)
  }

  case class Method(name: String, params: List[Param], ret: Ret)

  trait RootClassFactory[INTERFACE] {
    def className: String
    def cpp: String

    def tclass: RootAccessLibrary.TClass

    def newInstance: INTERFACE
  }
}

package object scaroot {
  def rootClassFactory[INTERFACE](cpp: String): RootClassFactory[INTERFACE] = macro rootClassFactoryImpl[INTERFACE]

  def rootClassFactoryImpl[INTERFACE : c.WeakTypeTag](c: Context)(cpp: c.Expr[String]): c.Expr[RootClassFactory[INTERFACE]] = {
    import c.universe._
    val interface = weakTypeOf[INTERFACE]

    val name = interface.toString.split('.').last

    val undefined = interface.declarations.
      filter(_.asInstanceOf[scala.reflect.internal.Symbols#Symbol].hasFlag(scala.reflect.internal.Flags.DEFERRED))

    val (methodsReflect, methodsReal) = undefined.toList.collect {case method: MethodSymbol =>
      val (paramsReflect, paramsReal) = method.paramss.flatten.map {p =>
        if (p.typeSignature =:= typeOf[Int])
          (q"IntParam(${p.name.toString})", q"${stringToTermName(p.name.toString)}: ${p.typeSignature}")
        else
          throw new Exception(p.typeSignature.toString)
      }.unzip

      val (retReflect, retReal) =
        if (method.returnType =:= typeOf[Int])
          (q"IntRet", method.returnType)
        else
          throw new Exception("oops 2")

      (q"Method(${method.name.toString}, List(..$paramsReflect), $retReflect)",
        q"def ${stringToTermName(method.name.toString)}(..$paramsReal): $retReal = 999")
    }.unzip

    println(methodsReflect)
    println(methodsReal)
    println(methodsReal.getClass)
    println(methodsReal.head.getClass)

    c.Expr[RootClassFactory[INTERFACE]](q"""
      import com.sun.jna.Memory
      import com.sun.jna.Native
      import com.sun.jna.Pointer
      import org.dianahep.scaroot._

      new RootClassFactory[$interface] {
        val className = $name
        val cpp = $cpp

        RootAccessLibrary.resetSignals()
        RootAccessLibrary.declare(cpp)
        val tclass = RootAccessLibrary.tclass(className)

        def newInstance: $interface = new $interface {




          ..$methodsReal
        }
      }
    """)
  }

}
