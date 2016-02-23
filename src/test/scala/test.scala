package test.scala.scaroot

import scala.collection.mutable
import scala.language.postfixOps

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import org.dianahep.scaroot._

class DefaultSuite extends FlatSpec with Matchers {
  "JNA" must "work" in {
    println("one")
    val file = RootTreeReaderLibrary.INSTANCE.newFile("/home/pivarski/fun/rio/TrackResonanceNtuple.root")
    println("two")
    val reader = RootTreeReaderLibrary.INSTANCE.newReader(file, "TrackResonanceNtuple/twoMuon")
    println("three")
    val mass = RootTreeReaderLibrary.INSTANCE.newValue_float(reader, "mass_mumu")
    println("four")
    var counter = 0
    while (RootTreeReaderLibrary.INSTANCE.readerNext(reader) > 0) {
      // println("five")
      // println(RootTreeReaderLibrary.INSTANCE.getValue_float(mass))
      val num = RootTreeReaderLibrary.INSTANCE.getValue_float(mass)
      println(num)
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
