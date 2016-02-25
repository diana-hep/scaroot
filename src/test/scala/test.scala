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

    println("zero")
    RootTreeReaderLibrary.resetSignals()
    println("one")
    val file = RootTreeReaderLibrary.newFile("/home/pivarski/fun/rio/TrackResonanceNtuple.root")
    println("two")
    val reader = RootTreeReaderLibrary.newReader(file, "TrackResonanceNtuple/twoMuon")
    println("three")
    val mass = RootTreeReaderLibrary.newValue_float(reader, "mass_mumu")
    val px = RootTreeReaderLibrary.newValue_float(reader, "px")
    val py = RootTreeReaderLibrary.newValue_float(reader, "py")
    val pz = RootTreeReaderLibrary.newValue_float(reader, "pz")
    println("four")
    val builder = new scala.collection.immutable.VectorBuilder[(Float, Float, Float, Float)]
    while (RootTreeReaderLibrary.readerNext(reader) > 0) {
      builder += Tuple4(RootTreeReaderLibrary.getValue_float(mass), RootTreeReaderLibrary.getValue_float(px), RootTreeReaderLibrary.getValue_float(py), RootTreeReaderLibrary.getValue_float(pz))
      // if (counter % 1000 == 0) {
      //   println("gc start")
      //   System.gc()
      //   println("gc end")
      // }
    }
    println("end")
    val vector = builder.result

    println("done", vector.size)
    Thread.sleep(5)

    vector.take(10).foreach(println)

    println("done done")
  }
}
