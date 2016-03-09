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
    val value: Pointer = new Memory(Native.getNativeSize(java.lang.Integer.TYPE))
    def apply(x: Int) = {
      value.setInt(0, x)
      value
    }
  }

  trait Ret
  case class IntRet() extends Ret {
    val value: Pointer = new Memory(Native.getNativeSize(java.lang.Integer.TYPE))
    def apply() = value.getInt(0)
  }

  case class Method(name: String, params: List[Param], ret: Ret, tclass: RootAccessLibrary.TClass) {
    val tmethod = RootAccessLibrary.tmethod(tclass, 0)   // FIXME
  }

  trait RootClassInstance[INTERFACE] {
    // def rootClassFactory: RootClassFactory[INTERFACE]
    // def externalMethods: List[Method]
    // def tmethods: Map[String, RootAccessLibrary.TMethod]
    def instance: Pointer
  }

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
      filter(x => x.isMethod  &&  x.asInstanceOf[scala.reflect.internal.Symbols#Symbol].hasFlag(scala.reflect.internal.Flags.DEFERRED))

    val definitions = undefined.toList.flatMap {case method: MethodSymbol =>
      val methodParams_ = List.newBuilder[c.universe.Typed]
      val rootArgs_ = List.newBuilder[c.universe.Tree]
      val argHolderNames_ = List.newBuilder[c.universe.TermName]
      val argHolderDefs_ = List.newBuilder[c.universe.Tree]
      val argHolderCalls_ = List.newBuilder[c.universe.Tree]

      method.paramss.flatten.foreach {p =>
        val paramTermName = stringToTermName(p.name.toString)

        val methodParam = q"$paramTermName: ${p.typeSignature}"

        val argHolderName = stringToTermName("__" + method.name.toString + "_param_" + p.name.toString)

        val rootArg = q"$argHolderName($paramTermName)"

        val argHolderDef =
          if (p.typeSignature =:= typeOf[Int])
            q"val $argHolderName = IntParam(${p.name.toString})"
          else
            throw new Exception("oops 1")

        val argHolderCall = q"$argHolderName($paramTermName)"

        methodParams_   += methodParam
        rootArgs_       += rootArg
        argHolderNames_ += argHolderName
        argHolderDefs_  += argHolderDef
        argHolderCalls_ += argHolderCall
      }

      val methodParams   = methodParams_.result
      val rootArgs       = rootArgs_.result
      val argHolderNames = argHolderNames_.result
      val argHolderDefs  = argHolderDefs_.result
      val argHolderCalls = argHolderCalls_.result

      val retHolderName = stringToTermName("__" + method.name.toString + "_ret")

      val (retHolderDef, retHolderCall) =
        if (method.returnType =:= typeOf[Int])
          (q"val $retHolderName = IntRet()", q"$retHolderName()")
        else
          throw new Exception("oops 2")

      val methodHolderName = stringToTermName("__" + method.name.toString + "_method")
      val methodHolderDef = q"val $methodHolderName = Method(${method.name.toString}, List(..$argHolderNames), $retHolderName, tclass)"

      val allRootArgs = List(q"$methodHolderName.tmethod", q"instance") ++ rootArgs ++ List(q"$retHolderName.value")

      val execute = q"""RootAccessLibrary.${stringToTermName(s"execute${argHolderDefs.size}")}"""

      val methodDef = q"def ${stringToTermName(method.name.toString)}(..$methodParams): ${method.returnType} = { $execute(..$allRootArgs); $retHolderName() }"
      
      argHolderDefs ++ List(retHolderDef, methodHolderDef, methodDef)
    }

    definitions.foreach(println)

    val out = c.Expr[RootClassFactory[INTERFACE]](q"""
      import com.sun.jna.Memory
      import com.sun.jna.Native
      import com.sun.jna.Pointer
      import org.dianahep.scaroot._

      new RootClassFactory[$interface] {
        val className = $name
        val cpp = $cpp.trim + "\n"

        RootAccessLibrary.resetSignals()
        RootAccessLibrary.declare(cpp)

        val tclass = RootAccessLibrary.tclass(className)

        def newInstance: $interface = new $interface with RootClassInstance[$interface] {
          val instance = RootAccessLibrary.newInstance(tclass)
          ..$definitions
        }
      }
    """)

    println(out)

    out
  }

}
