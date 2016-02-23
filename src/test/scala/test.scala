package test.scala.scaroot

import scala.collection.mutable
import scala.language.postfixOps

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import org.dianahep.scaroot._

class DefaultSuite extends FlatSpec with Matchers {
  "JNA" must "work" in {
    println(RootTreeReaderLibrary.INSTANCE.test1(5));
  }
}
