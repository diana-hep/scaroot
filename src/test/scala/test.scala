package test.scala.scaroot

import scala.collection.mutable
import scala.language.postfixOps

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import org.bridj.BridJ
import org.bridj.Pointer
import org.bridj.Pointer._
import org.bridj.ann.Library

import org.dianahep.scaroot._

@Library("libRootTreeReader")
object RootTreeReader {
  BridJ.register()
  @native def hello(): Int
}

class DefaultSuite extends FlatSpec with Matchers {
  "Bridj" must "work" in {
    println("BEGIN")
    println(RootTreeReader.hello())
    println("END")
  }
}
