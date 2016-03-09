package test.scala.scaroot

import scala.collection.mutable
import scala.language.postfixOps

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

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

    val result = RootAccessLibrary.execute(plus, instance, 3, 7)
    println(result)

  }
}
