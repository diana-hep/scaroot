package test.scala.scaroot

import scala.collection.mutable
import scala.language.postfixOps

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import com.sun.jna.Pointer
import com.sun.jna.ptr.PointerByReference
import com.sun.jna.Memory
import com.sun.jna.Native

import org.dianahep.scaroot._

class DefaultSuite extends FlatSpec with Matchers {
  "RootAccess" must "work" in {
    RootAccessLibrary.resetSignals()

    RootAccessLibrary.declare("""
class Something {
public:
  int plus(int x, int y) { return x + y; }
};
""")

    val tclass = RootAccessLibrary.tclass("Something")
    println("tclass", tclass.getClass)


    val instance = RootAccessLibrary.newInstance(tclass)

    val numMethods = RootAccessLibrary.numMethods(tclass)

    val tmethods = 0 until numMethods map {methodIndex => RootAccessLibrary.tmethod(tclass, methodIndex)}

    tmethods foreach {tmethod =>
      // print(RootAccessLibrary.tmethodName(tmethod))
      // print(" ")
      // print(RootAccessLibrary.tmethodNumArgs(tmethod))
      // print(" ")

      val args = 0 until RootAccessLibrary.tmethodNumArgs(tmethod) map {argIndex => RootAccessLibrary.tmethodArg(tmethod, argIndex)}
      // println(args.mkString(" "))
    }

    val plus = RootAccessLibrary.tmethod(tclass, 0)

    val arg0 = new Memory(Native.getNativeSize(java.lang.Integer.TYPE))
    val arg1 = new Memory(Native.getNativeSize(java.lang.Integer.TYPE))
    val ret = new Memory(Native.getNativeSize(java.lang.Integer.TYPE))

    arg0.setInt(0, 3)
    arg1.setInt(0, 7)
    ret.setInt(0, 999)

    RootAccessLibrary.execute2(plus, instance, arg0, arg1, ret)

  }

  "Scala macros" must "work" in {
    trait MyInterface {
      def two(x: Int): Int
    }

    val part1 = """
class MyInterface {
"""
    val part2 = """
public:
"""
    val part3 = """
  int two(int x) { return x + 10; }
};
"""

    val factory = rootClassFactory[MyInterface](part1 + part2 + part3)
    println(factory.className)
    println(factory.cpp)

    val instance = factory.newInstance

    println(instance)
    println(instance.two(2))

  }
}
