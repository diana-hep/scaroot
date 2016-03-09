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
  Something() {}
  int plus(int x, int y) { return x + y; }
};
"""))

    val tclass = TClass.GetClass(pointerToCString("Something"), true, false);

    val instance = tclass.get.New(TClass.ENewType.fromValue(1), false)

    val tlist = tclass.get.GetListOfMethods(true)

    val tlistiter = new TListIter(tlist, true)

    var tmethod = tlistiter.Next()

    while (tmethod != Pointer.NULL) {
      println(tmethod.as(classOf[TFunction]))

      // println(tmethod.as(classOf[TFunction]).get.GetDeclId())
      tmethod = tlistiter.Next()
    }



  }
}
