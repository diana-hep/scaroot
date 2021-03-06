package org.dianahep

import scala.language.experimental.macros
// import scala.reflect.macros.blackbox.Context
import scala.reflect.macros.Context

import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer

package scaroot {
  /////////////////////////////////////////////// Param

  trait Param {
    def name: String
    def value: Pointer
    def hasCppType(x: String): Boolean
  }

  case class BooleanParam(name: String) extends Param {
    val value = new Memory(Native.getNativeSize(java.lang.Byte.TYPE))
    def apply(x: Boolean) = {
      value.setByte(0, if (x) 1 else 0)
      value
    }
    def hasCppType(x: String) = x == "bool"
    override def toString() = s"""BooleanParam("$name")"""
  }

  case class ByteParam(name: String) extends Param {
    val value = new Memory(Native.getNativeSize(java.lang.Byte.TYPE))
    def apply(x: Byte) = {
      value.setByte(0, x)
      value
    }
    def hasCppType(x: String) = x == "char"
    override def toString() = s"""ByteParam("$name")"""
  }

  case class ShortParam(name: String) extends Param {
    val value = new Memory(Native.getNativeSize(java.lang.Short.TYPE))
    def apply(x: Short) = {
      value.setShort(0, x)
      value
    }
    def hasCppType(x: String) = x == "short"
    override def toString() = s"""ShortParam("$name")"""
  }

  case class IntParam(name: String) extends Param {
    val value = new Memory(Native.getNativeSize(java.lang.Integer.TYPE))
    def apply(x: Int) = {
      value.setInt(0, x)
      value
    }
    def hasCppType(x: String) = x == "int"
    override def toString() = s"""IntParam("$name")"""
  }

  case class LongParam(name: String) extends Param {
    val value = new Memory(Native.getNativeSize(java.lang.Long.TYPE))
    def apply(x: Long) = {
      value.setLong(0, x)
      value
    }
    def hasCppType(x: String) = x == "long"
    override def toString() = s"""LongParam("$name")"""
  }

  case class FloatParam(name: String) extends Param {
    val value = new Memory(Native.getNativeSize(java.lang.Float.TYPE))
    def apply(x: Float) = {
      value.setFloat(0, x)
      value
    }
    def hasCppType(x: String) = x == "float"
    override def toString() = s"""FloatParam("$name")"""
  }

  case class DoubleParam(name: String) extends Param {
    val value = new Memory(Native.getNativeSize(java.lang.Double.TYPE))
    def apply(x: Double) = {
      value.setDouble(0, x)
      value
    }
    def hasCppType(x: String) = x == "double"
    override def toString() = s"""DoubleParam("$name")"""
  }

  case class StringParam(name: String) extends Param {
    // C++ only looks for a pointer to the string data, so we only
    // allocate enough space for a pointer.
    val value = new Memory(Native.getNativeSize(classOf[Pointer]))
    // But of course, we do need a buffer to put the actual string
    // data, and it must be off-heap for C++ to see it.
    private var buffer: Memory = null
    def apply(x: String): Pointer = {
      if (buffer == null  ||  buffer.size < x.size + 1)
        buffer = new Memory(x.size + 1)     // Only enlarge the size to avoid frequent reallocations.
      try {
        buffer.setString(0, x)              // This can fail if x has wide UTF-8 characters.
      }
      catch {
        case _: java.lang.IndexOutOfBoundsException =>
          buffer = new Memory(2 * buffer.size)    // In that rare case, exponentially grow the allocation
          return apply(x)                         // until it fits (recursive, but it WILL terminate!).
      }
      value.setPointer(0, buffer)
      value    // return a pointer to the buffer, not the buffer pointer itself
    }
    def hasCppType(x: String) = x.replace("const", "").replace(" ", "") == "char*"
    override def toString() = s"""StringParam("$name")"""
  }

  case class PointerParam(name: String) extends Param {
    val value = new Memory(Native.getNativeSize(classOf[Pointer]))
    def apply(x: Pointer): Pointer = {
      value.setPointer(0, x)
      value
    }
    def hasCppType(x: String) = x.contains('*')
    override def toString() = s"""PointerParam("$name")"""
  }

  /////////////////////////////////////////////// Ret

  trait Ret {
    def value: Pointer
    def hasCppType(x: String): Boolean
  }

  case class BooleanRet() extends Ret {
    val value = new Memory(Native.getNativeSize(java.lang.Byte.TYPE))
    def apply() = value.getByte(0) != 0
    def hasCppType(x: String) = x == "bool"
  }

  case class ByteRet() extends Ret {
    val value = new Memory(Native.getNativeSize(java.lang.Byte.TYPE))
    def apply() = value.getByte(0)
    def hasCppType(x: String) = x == "char"
  }

  case class ShortRet() extends Ret {
    val value = new Memory(Native.getNativeSize(java.lang.Short.TYPE))
    def apply() = value.getShort(0)
    def hasCppType(x: String) = x == "short"
  }

  case class IntRet() extends Ret {
    val value = new Memory(Native.getNativeSize(java.lang.Integer.TYPE))
    def apply() = value.getInt(0)
    def hasCppType(x: String) = x == "int"
  }

  case class LongRet() extends Ret {
    val value = new Memory(Native.getNativeSize(java.lang.Long.TYPE))
    def apply() = value.getLong(0)
    def hasCppType(x: String) = x == "long"
  }

  case class FloatRet() extends Ret {
    val value = new Memory(Native.getNativeSize(java.lang.Float.TYPE))
    def apply() = value.getFloat(0)
    def hasCppType(x: String) = x == "float"
  }

  case class DoubleRet() extends Ret {
    val value = new Memory(Native.getNativeSize(java.lang.Double.TYPE))
    def apply() = value.getDouble(0)
    def hasCppType(x: String) = x == "double"
  }

  case class StringRet() extends Ret {
    // C++ only writes a pointer to the string data, so we only
    // allocate enough space for a pointer.
    val value = new Memory(Native.getNativeSize(classOf[Pointer]))
    // Pray that there's something there and it's NULL-terminated...
    def apply() = value.getPointer(0).getString(0)
    def hasCppType(x: String) = x.replace("const", "").replace(" ", "") == "char*"
  }

  case class PointerRet() extends Ret {
    val value = new Memory(Native.getNativeSize(classOf[Pointer]))
    def apply() = value.getPointer(0)
    def hasCppType(x: String) = x.contains('*')
  }

  case class UnitRet() extends Ret {
    val value = Pointer.NULL
    def apply() { }
    def hasCppType(x: String) = x == "void"
  }

  /////////////////////////////////////////////// Method

  class Method(val name: String, val params: List[Param], val ret: Ret, private val tclass: RootAccessLibrary.TClass = null) {
    val tmethod =
      if (tclass != null)
        (0 until RootAccessLibrary.numMethods(tclass)).map(RootAccessLibrary.tmethod(tclass, _)).find({tm =>
          RootAccessLibrary.tmethodName(tm) == name  &&
          RootAccessLibrary.tmethodNumArgs(tm) == params.size  &&
            (0 until RootAccessLibrary.tmethodNumArgs(tm)).map(RootAccessLibrary.tmethodArgType(tm, _)).zip(params).forall({case (rootTypeName, param) =>
              param.hasCppType(rootTypeName)
            })  &&
          ret.hasCppType(RootAccessLibrary.tmethodRetType(tm))
        }).getOrElse(null)
      else
        null

    override def toString() = s"""Method("$name", $params, $ret)"""

    override def equals(x: Any): Boolean = x match {
      case that: Method if (this.name == that.name  &&  this.params == that.params  &&  this.ret == that.ret) =>
        if (this.tclass != null  &&  that.tclass != null)
          this.tclass == that.tclass
        else
          true
      case _ => false
    }
    override def hashCode(): Int = java.util.Objects.hash(name, params, ret)
  }

  object Method {
    def unapply(x: Method) = Some((x.name, x.params, x.ret))
    def apply(name: String, params: List[Param], ret: Ret, tclass: RootAccessLibrary.TClass = null) =
      new Method(name, params, ret, tclass)
  }

  /////////////////////////////////////////////// RootClass and RootInstance

  trait RootInstance[INTERFACE] {
    def rootClass: RootClass[INTERFACE]
    def rootMethods: List[Method]
    def rootInstance: Pointer
  }

  object RootInstance {
    def unapply(x: RootInstance[_]) = Some(x.rootMethods)
  }

  trait RootClass[INTERFACE] extends java.io.Serializable {
    def className: String
    def cpp: String
    def tclass: RootAccessLibrary.TClass
    def newInstance: INTERFACE
    override def equals(x: Any): Boolean = x match {
      case that: RootClass[_] if (this.className == that.className  &&  this.cpp == that.cpp) => true
      case _ => false
    }
    override def hashCode(): Int = java.util.Objects.hash(className, cpp)
  }

  object RootClass {
    var namespaceNumber = 0

    def unapply(x: RootClass[_]) = Some(x.cpp)

    def apply[INTERFACE](cpp: String): RootClass[INTERFACE] = macro newClassImpl[INTERFACE]

    def newClass[INTERFACE](cpp: String): RootClass[INTERFACE] = macro newClassImpl[INTERFACE]

    def newClassImpl[INTERFACE : c.WeakTypeTag](c: Context)(cpp: c.Expr[String]): c.Expr[RootClass[INTERFACE]] = {
      import c.universe._
      val interface = weakTypeOf[INTERFACE]

      val name = interface.toString.split('.').last

      val undefined = interface.declarations.
        filter(x => x.isMethod  &&  x.asInstanceOf[scala.reflect.internal.Symbols#Symbol].hasFlag(scala.reflect.internal.Flags.DEFERRED))

      val definitions_ = List.newBuilder[c.universe.Tree]
      val rootMethods_ = List.newBuilder[c.universe.TermName]

      undefined.toList.foreach {case method: MethodSymbol =>
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
            if (p.typeSignature =:= typeOf[Boolean])
              q"val $argHolderName = BooleanParam(${p.name.toString})"
            else if (p.typeSignature =:= typeOf[Byte])
              q"val $argHolderName = ByteParam(${p.name.toString})"
            else if (p.typeSignature =:= typeOf[Short])
              q"val $argHolderName = ShortParam(${p.name.toString})"
            else if (p.typeSignature =:= typeOf[Int])
              q"val $argHolderName = IntParam(${p.name.toString})"
            else if (p.typeSignature =:= typeOf[Long])
              q"val $argHolderName = LongParam(${p.name.toString})"
            else if (p.typeSignature =:= typeOf[Float])
              q"val $argHolderName = FloatParam(${p.name.toString})"
            else if (p.typeSignature =:= typeOf[Double])
              q"val $argHolderName = DoubleParam(${p.name.toString})"
            else if (p.typeSignature =:= typeOf[String])
              q"val $argHolderName = StringParam(${p.name.toString})"
            else if (p.typeSignature =:= typeOf[Pointer])
              q"val $argHolderName = PointerParam(${p.name.toString})"
            else
              throw new IllegalArgumentException(s"""
**********************************************************************************************************
Methods that have been deferred to C++ in a RootClass must have signatures that consist of only primitive types:

    Boolean, Byte, Short, Int, Long, Float, Double, String, or an opaque com.sun.jna.Pointer to C++ data.

Encountered type "${p.typeSignature}" in parameter "${p.name}" of method "${method.name}".
**********************************************************************************************************""")

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

        val retHolderDef =
          if (method.returnType =:= typeOf[Boolean])
            q"val $retHolderName = BooleanRet()"
          else if (method.returnType =:= typeOf[Byte])
            q"val $retHolderName = ByteRet()"
          else if (method.returnType =:= typeOf[Short])
            q"val $retHolderName = ShortRet()"
          else if (method.returnType =:= typeOf[Int])
            q"val $retHolderName = IntRet()"
          else if (method.returnType =:= typeOf[Long])
            q"val $retHolderName = LongRet()"
          else if (method.returnType =:= typeOf[Float])
            q"val $retHolderName = FloatRet()"
          else if (method.returnType =:= typeOf[Double])
            q"val $retHolderName = DoubleRet()"
          else if (method.returnType =:= typeOf[String])
            q"val $retHolderName = StringRet()"
          else if (method.returnType =:= typeOf[Pointer])
            q"val $retHolderName = PointerRet()"
          else if (method.returnType =:= typeOf[Unit])
            q"val $retHolderName = UnitRet()"
          else
            throw new IllegalArgumentException(s"""
**********************************************************************************************************
Methods that have been deferred to C++ in a RootClass must return only primitive types:

    Boolean, Byte, Short, Int, Long, Float, Double, String, or an opaque com.sun.jna.Pointer to C++ data.

Encountered type "${method.returnType}" in method "${method.name}".
**********************************************************************************************************""")

        val methodHolderName = stringToTermName("__" + method.name.toString + "_method")
        val methodHolderDef = q"val $methodHolderName = new Method(${method.name.toString}, List(..$argHolderNames), $retHolderName, tclass)"

        val allRootArgs = List(q"$methodHolderName.tmethod", q"rootInstance") ++ rootArgs ++ List(q"$retHolderName.value")

        val execute = q"""RootAccessLibrary.${stringToTermName(s"execute${argHolderDefs.size}")}"""

        val methodDef = q"def ${stringToTermName(method.name.toString)}(..$methodParams): ${method.returnType} = { $execute(..$allRootArgs); $retHolderName() }"

        definitions_ ++= argHolderDefs
        definitions_ += retHolderDef
        definitions_ += methodHolderDef
        definitions_ += methodDef

        rootMethods_ += methodHolderName
      }

      val definitions = definitions_.result
      val rootMethods = rootMethods_.result

      c.Expr[RootClass[INTERFACE]](q"""
        import com.sun.jna.Memory
        import com.sun.jna.Native
        import com.sun.jna.Pointer
        import org.dianahep.scaroot._

        new RootClass[$interface] {
          val className = $name
          val cpp = $cpp.trim + "\n"

          @scala.transient
          lazy val tclass = {
            RootAccessLibrary.resetSignals()
            RootAccessLibrary.declare("namespace RootNamespace" + RootClass.namespaceNumber.toString + " {\n" + cpp + "}")
            val out = RootAccessLibrary.tclass("RootNamespace" + RootClass.namespaceNumber.toString + "::" + className)
            RootClass.namespaceNumber += 1
            out
          }

          private val THIS = this

          def newInstance: $interface = new $interface with RootInstance[$interface] {
            val rootClass = THIS
            val rootInstance = RootAccessLibrary.newInstance(tclass)

            ..$definitions

            def rootMethods = List(..$rootMethods)
          }
        }
      """)
    }
  }
}
