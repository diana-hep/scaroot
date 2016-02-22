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
  "Bridj" must "work" in {
    println("BEGIN")
    val rootTreeReader = new RootTreeReader(pointerToCString("one"), pointerToCString("two"));
    println("MIDDLE")
    println(rootTreeReader.hello())
    println("END")
  }
}
