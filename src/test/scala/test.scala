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
    val fileLocation = pointerToCString("root://cmsxrootd.fnal.gov//store/user/pivarski/TrackResonanceNtuple.root")
    val treeLocation = pointerToCString("TrackResonanceNtuple/twoMuon")
    val namesTypes = Seq("mass_mumu" -> "Float", "px" -> "Float", "py" -> "Float", "pz" -> "Float")
    val names: Pointer[Pointer[java.lang.Byte]] = pointerToArray(namesTypes map {case (n, _) => pointerToCString(n)} toArray)
    val types: Pointer[Pointer[java.lang.Byte]] = pointerToArray(namesTypes map {case (_, t) => pointerToCString(t)} toArray)

    val rootTreeReader = new RootTreeReader(fileLocation, treeLocation, namesTypes.size, names, types);
    println("END")
  }
}
