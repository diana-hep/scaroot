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
    // System.loadLibrary("Core");
    // System.loadLibrary("RIO");
    // System.loadLibrary("Net");
    // System.loadLibrary("Hist");
    // System.loadLibrary("Graf");
    // System.loadLibrary("Graf3d");
    // System.loadLibrary("Gpad");
    // System.loadLibrary("Tree");
    // System.loadLibrary("Rint");
    // System.loadLibrary("Postscript");
    // System.loadLibrary("Matrix");
    // System.loadLibrary("Physics");
    // System.loadLibrary("MathCore");
    // System.loadLibrary("Thread");
    // System.loadLibrary("MultiProc");
    // System.loadLibrary("m");
    // System.loadLibrary("dl");

    println("zero")

    // turn off ROOT's signal handling to avoid conflicts with Java's
    BridJ.getNativeLibrary("Core").getSymbolPointer("gSystem").as(classOf[TUnixSystem]).get.ResetSignals()

    println("one")

    val gInterpreter = TInterpreter.Instance.as(classOf[TCling])

    println("two")

    gInterpreter.get.Declare(pointerToCString("""
class Something {
public:
  Something() {
    TH1F hist("hey", "there", 100, 0, 1);
  }
  int plus(int x, int y) { return x + y; }
};
"""))

    println("three")
  }
}
