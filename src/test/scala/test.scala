package test.scala.scaroot

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import com.sun.jna._

class DefaultSuite extends FlatSpec with Matchers {
  "Experiments" should "do something, maybe" in {
    trait LibM extends Library {
      def sin(d: Double): Double
    }
    val libm = Native.loadLibrary("m", classOf[LibM]).asInstanceOf[LibM]

    object LibM2 {
      Native.register("m")

      @native
      def sin(d: Double): Double
    }

    println(s"sin(0.5) is ${libm.sin(0.5)} vs ${LibM2.sin(0.5)} vs ${Math.sin(0.5)}")

  }

}
