package test.scala.scaroot

import scala.collection.mutable
import scala.language.postfixOps

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import org.bridj.Pointer
import org.bridj.Pointer._

import org.dianahep.scaroot._

class DefaultSuite extends FlatSpec with Matchers {
  "stuff" must "work" in {
    println("one")

    val gInterpreter = TInterpreter.Instance.as(classOf[TCling])

    println("two")

    gInterpreter.get.Declare(pointerToCString("""
class Something {
public:
  Something() { }
  int plus(int x, int y) { return x + y; }
};
"""))

    println("three")
  }
}
