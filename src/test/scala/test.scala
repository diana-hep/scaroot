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
    // val fileLocation = pointerToCString("root://cmsxrootd.fnal.gov//store/user/pivarski/TrackResonanceNtuple.root")
    // val treeLocation = pointerToCString("TrackResonanceNtuple/twoMuon")
    // val namesTypes = Seq("mass_mumu" -> "float", "px" -> "float", "py" -> "float", "pz" -> "float")
    // val names: Pointer[Pointer[java.lang.Byte]] = pointerToArray(namesTypes map {case (n, _) => pointerToCString(n)} toArray)
    // val types: Pointer[Pointer[java.lang.Byte]] = pointerToArray(namesTypes map {case (_, t) => pointerToCString(t)} toArray)

    // val rootTreeReader = new RootTreeReader(fileLocation, treeLocation, namesTypes.size, names, types);

    val rootTreeReader = new RootTreeReader

    var counter = 0;

    // val builder = new scala.collection.immutable.VectorBuilder[(Float, Float, Float, Float)]
    while (rootTreeReader.next()) {
      val tuple = (counter, rootTreeReader.get_float(0), rootTreeReader.get_float(1), rootTreeReader.get_float(2), rootTreeReader.get_float(3))
      // builder += tuple
      println(tuple)
      counter += 1;
      if (counter % 1000 == 0) {
        println("System.gc()")
        System.gc()
        println("System.gc() done")
      }
    }
    println("end")
    // val vector = builder.result

    // println("done", vector.size)
    // Thread.sleep(5)

    // vector.foreach(println)

    // println("done done")
  }
}
