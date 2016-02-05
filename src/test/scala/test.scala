package test.scala.scaroot

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import org.dianahep.scaroot._

class DefaultSuite extends FlatSpec with Matchers {
  "Shared object" should "load and show some TFile stuff" in {
    val pointer = SharedObject.new_TFile("/opt/root/test/Event.root")
    println(s"pointer value $pointer")
    SharedObject.TFile_ls(pointer)
    println(s"see a listing?")
    SharedObject.delete_TFile(pointer)
    println(s"still here?")

  }
}
