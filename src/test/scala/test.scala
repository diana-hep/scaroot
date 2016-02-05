package test.scala.scaroot

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import com.sun.jna._

class DefaultSuite extends FlatSpec with Matchers {
  "Shared object" should "load and show some TFile stuff" in {
    object SharedObject extends Library {
      Native.register("target/classes/scaroot.so")
      @native def new_TFile(fileName: String): Long
      @native def delete_TFile(pointer: Long): Unit
      @native def TFile_ls(pointer: Long): Unit
    }

    val pointer = SharedObject.new_TFile("/opt/root/test/Event.root")
    println(s"pointer value $pointer")
    SharedObject.TFile_ls(pointer)
    println(s"see a listing?")
    SharedObject.delete_TFile(pointer)
    println(s"still here?")

  }
}
