package org.dianahep

import scala.language.experimental.macros
// import scala.reflect.macros.blackbox.Context
import scala.reflect.macros.Context

// import com.sun.jna.Pointer
// import com.sun.jna.ptr.PointerByReference
// import com.sun.jna.Memory
// import com.sun.jna.Native

package scaroot {
  trait RootClassFactory[INTERFACE] {
    def className: String
    def cpp: String
    def newInstance: INTERFACE
  }
}

package object scaroot {
  // RootAccessLibrary.resetSignals()

  def rootClassFactory[INTERFACE](cpp: String): RootClassFactory[INTERFACE] = macro rootClassFactoryImpl[INTERFACE]

  def rootClassFactoryImpl[INTERFACE : c.WeakTypeTag](c: Context)(cpp: c.Expr[String]): c.Expr[RootClassFactory[INTERFACE]] = {
    import c.universe._
    val interface = weakTypeOf[INTERFACE]

    val name = interface.toString.split('.').last

    c.Expr[RootClassFactory[INTERFACE]](q"""
      new RootClassFactory[$interface] {
        def className: String = $name
        def cpp: String = $cpp
        def newInstance: $interface = new $interface {
          def hello(x: String): String = "hey " + x
        }
      }
    """)
  }

}
