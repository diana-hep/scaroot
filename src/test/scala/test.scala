package test.scala.scaroot

import scala.collection.mutable
import scala.language.postfixOps

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import com.sun.jna.Native

import org.dianahep.scaroot._

class DefaultSuite extends FlatSpec with Matchers {
  "JNA" must "work" in {
    // Native.setProtected(true)
    // println("Native.isProtected", Native.isProtected)

    val lib = RootTreeReaderLibrary.INSTANCE

    println("zero")
    lib.resetSignals()
    println("one")
    val file = lib.newFile("/home/pivarski/fun/rio/TrackResonanceNtuple.root")
    println("two")
    val reader = lib.newReader(file, "TrackResonanceNtuple/twoMuon")
    println("three")
    val mass = lib.newValue_float(reader, "mass_mumu")
    val px = lib.newValue_float(reader, "px")
    val py = lib.newValue_float(reader, "py")
    val pz = lib.newValue_float(reader, "pz")
    println("four")
    var counter = 0
    while (lib.readerNext(reader) > 0) {
      println("five")
      println(lib.getValue_float(mass), lib.getValue_float(px), lib.getValue_float(py), lib.getValue_float(pz), System.getenv.get("LD_PRELOAD"))
      counter += 1
      if (counter % 1000 == 0) {
        println("gc start")
        System.gc()
        println("gc end")
      }
    }
    println("six")
  }
}
