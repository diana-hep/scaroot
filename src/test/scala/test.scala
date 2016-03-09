package test.scala.scaroot

import scala.collection.mutable
import scala.language.postfixOps

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.ptr.PointerByReference

import org.dianahep.scaroot._

class DefaultSuite extends FlatSpec with Matchers {
  "stuff" must "work" in {
    RootAccessLibrary.resetSignals()

    RootAccessLibrary.declare("""
class Something {
public:
  int plus(int x, int y) { return x + y; }
};
""")

    val tclass = RootAccessLibrary.tclass("Something")
    println(tclass)

    val instance = RootAccessLibrary.newInstance(tclass)
    println(instance)

    val numMethods = RootAccessLibrary.numMethods(tclass)
    println(numMethods)

    val tmethods = 0 until numMethods map {methodIndex => RootAccessLibrary.tmethod(tclass, methodIndex)}

    tmethods foreach {tmethod =>
      print(RootAccessLibrary.tmethodName(tmethod))
      print(" ")
      print(RootAccessLibrary.tmethodNumArgs(tmethod))
      print(" ")

      val args = 0 until RootAccessLibrary.tmethodNumArgs(tmethod) map {argIndex => RootAccessLibrary.tmethodArg(tmethod, argIndex)}
      println(args.mkString(" "))
    }

    val plus = RootAccessLibrary.tmethod(tclass, 0)
    println(plus)

    // val argv = new Memory(2 * Native.getNativeSize(java.lang.Integer.TYPE))
    // argv.setInt(0 * Native.getNativeSize(java.lang.Integer.TYPE), 3)
    // argv.setInt(1 * Native.getNativeSize(java.lang.Integer.TYPE), 8)
    // println(argv)
    // println(argv.getInt(0 * Native.getNativeSize(java.lang.Integer.TYPE)))
    // println(argv.getInt(1 * Native.getNativeSize(java.lang.Integer.TYPE)))

    // val ret = new Memory(1 * Native.getNativeSize(java.lang.Integer.TYPE))
    // ret.setInt(0 * Native.getNativeSize(java.lang.Integer.TYPE), 9)
    // println(ret)
    // println(ret.getInt(0))

    // RootAccessLibrary.execute(plus, instance, 2, new PointerByReference(argv), ret)
    // println(ret)
    // println(ret.getInt(0))

    val result = RootAccessLibrary.execute(plus, instance, 3, 7)
    println(result)

  }
}
