package org.dianahep

import scala.language.experimental.macros
// import scala.reflect.macros.blackbox.Context
import scala.reflect.macros.Context

import com.sun.jna.Pointer
import com.sun.jna.ptr.PointerByReference
import com.sun.jna.Memory
import com.sun.jna.Native

package scaroot {
  trait Param {
    def name: String
  }
  case class IntParam(name: String) extends Param

  trait Ret
  case object IntRet extends Ret

  case class Method(name: String, params: List[Param], ret: Ret)

  trait RootClassFactory[INTERFACE] {
    def className: String
    def methods: List[Method]
    def cpp: String
    def newInstance: INTERFACE
  }
}

package object scaroot {
  RootAccessLibrary.resetSignals()

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

    c.Expr[RootClassFactory[INTERFACE]](q"""
      new RootClassFactory[$interface] {
        val className = $name
        val methods = List(..$methodsReflect)
        val cpp = $cpp
        def newInstance: $interface = new $interface {
          ..$methodsReal
        }
      }
    """)
  }

}
