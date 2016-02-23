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
    Native.setProtected(true)
    println("Native.isProtected", Native.isProtected)

    val lib = RootTreeReaderLibrary.INSTANCE

    println("one")
    val file = lib.newFile("/home/pivarski/fun/rio/TrackResonanceNtuple.root")
    println("two")
    val reader = lib.newReader(file, "TrackResonanceNtuple/twoMuon")
    println("three")
    val mass = lib.newValue_float(reader, "mass_mumu")
    println("four")
    var counter = 0
    while (lib.readerNext(reader) > 0) {
      // println("five")
      // println(lib.getValue_float(mass))
      val num = lib.getValue_float(mass)
      println(num)
      counter += 1
      // if (counter % 1000 == 0) {
      //   println("gc start")
      //   System.gc()
      //   println("gc end")
      // }
    }
    println("six")
  }
}
