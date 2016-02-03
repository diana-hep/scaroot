package test.scala.scaroot

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

class DefaultSuite extends FlatSpec with Matchers {
  "Nothing" should "do anything" in {
    println("wowie")
  }

}
