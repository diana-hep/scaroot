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
    val fileLocation = pointerToCString("root://cmsxrootd.fnal.gov//store/user/pivarski/TrackResonanceNtuple.root")
    val treeLocation = pointerToCString("TrackResonanceNtuple/twoMuon")
    val namesTypes = Seq("mass_mumu" -> "float", "px" -> "float", "py" -> "float", "pz" -> "float")
    val names: Pointer[Pointer[java.lang.Byte]] = pointerToArray(namesTypes map {case (n, _) => pointerToCString(n)} toArray)
    val types: Pointer[Pointer[java.lang.Byte]] = pointerToArray(namesTypes map {case (_, t) => pointerToCString(t)} toArray)

    val rootTreeReader = new RootTreeReader(fileLocation, treeLocation, namesTypes.size, names, types);

    val builder = new scala.collection.immutable.VectorBuilder[(Float, Float, Float, Float)]
    while (rootTreeReader.next()) {
      // val tuple = (rootTreeReader.get(0).getFloat, rootTreeReader.get(1).getFloat, rootTreeReader.get(2).getFloat, rootTreeReader.get(3).getFloat)
      val tuple = (rootTreeReader.get(0), rootTreeReader.get(1), rootTreeReader.get(2), rootTreeReader.get(3))
      builder += tuple
    }
    val vector = builder.result

    println("done", vector.size)
    Thread.sleep(5)

    vector.foreach(println)

    println("done done")
  }
}
