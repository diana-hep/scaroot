package test.scala.scaroot

import scala.collection.mutable
import scala.language.postfixOps

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import org.bridj.BridJ
import org.bridj.Pointer
import org.bridj.Pointer._

import org.dianahep.scaroot._

class DefaultSuite extends FlatSpec with Matchers {
  "stuff" must "work" in {
    // turn off ROOT's signal handling to avoid conflicts with Java's
    BridJ.getNativeLibrary("Core").getSymbolPointer("gSystem").as(classOf[TUnixSystem]).get.ResetSignals()

    val gInterpreter = TInterpreter.Instance.as(classOf[TCling])

    gInterpreter.get.Declare(pointerToCString("""
class Something {
public:
  Something() {
    TH1F hist("hey", "there", 100, 0, 1);
  }
  int plus(int x, int y) { return x + y; }
};
"""))

    val tclass = TClass.GetClass(pointerToCString("Something"), true, false);

    val tlist = tclass.get.GetListOfAllPublicMethods(true)

    val tlistiter = Pointer.getPointer(new TListIter(tlist, true))

    // val titer1 = new TIter(tlistiter.as(classOf[TIter]).get)

    // val titer2 = titer1.Begin()

    println(tlistiter.get.Next())
    println(tlistiter.get.Next())
    println(tlistiter.get.Next())
    println(tlistiter.get.Next())
    println(tlistiter.get.Next())
    println(tlistiter.get.Next())
    println(tlistiter.get.Next())
    println(tlistiter.get.Next())
    println(tlistiter.get.Next())
    println(tlistiter.get.Next())

  }
}
