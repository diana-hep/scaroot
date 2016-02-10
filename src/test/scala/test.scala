package test.scala.scaroot

import scala.collection.mutable
import scala.language.postfixOps

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import hep.io.root._
import hep.io.root.interfaces._

import org.dianahep.scaroot._
import org.dianahep.scaroot.api._
import org.dianahep.scaroot.freehep._

class DefaultSuite extends FlatSpec with Matchers {
  "Shared object" should "load and show some TFile stuff" in {
    val pointer = SharedObject.new_TFile("/opt/root/test/Event.root")
    println(s"pointer value $pointer")
    SharedObject.TFile_ls(pointer)
    println(s"see a listing?")
    SharedObject.delete_TFile(pointer)
    println(s"still here?")
  }

  "FreeHEP" should "open verysimple.root" in {
    val reader = new RootFileReader("src/test/resources/verysimple.root")
    val tree = reader.get("ntuple").asInstanceOf[TTree]
    tree.getEntries should be (5)

    val leaves = tree.getLeaves
    leaves.getLowerBound should be (0)
    leaves.getUpperBound should be (3)

    val lookup = leaves.getLowerBound until leaves.getUpperBound map {i =>
      val element = leaves.getElementAt(i)
      (element.asInstanceOf[TNamed].getName, element.asInstanceOf[TLeaf])
    } toMap

    val x = lookup("x").asInstanceOf[TLeafF]
    val y = lookup("y").asInstanceOf[TLeafF]
    val z = lookup("z").asInstanceOf[TLeafF]

    (x.getValue(0), y.getValue(0), z.getValue(0)) should be ((1.0, 2.0, 3.0))
    (x.getValue(1), y.getValue(1), z.getValue(1)) should be ((4.0, 5.0, 6.0))
    (x.getValue(2), y.getValue(2), z.getValue(2)) should be ((7.0, 8.0, 9.0))
    (x.getValue(3), y.getValue(3), z.getValue(3)) should be ((10.0, 11.0, 12.0))
    (x.getValue(4), y.getValue(4), z.getValue(4)) should be ((1.0, 2.0, 3.0))
  }

  it should "open simple.root" in {
    val reader = new RootFileReader("src/test/resources/simple.root")
    val tree = reader.get("tree").asInstanceOf[TTree]
    tree.getEntries should be (4)

    val leaves = tree.getLeaves
    leaves.getLowerBound should be (0)
    leaves.getUpperBound should be (3)

    val lookup = leaves.getLowerBound until leaves.getUpperBound map {i =>
      val element = leaves.getElementAt(i)
      (element.asInstanceOf[TNamed].getName, element.asInstanceOf[TLeaf])
    } toMap

    val one = lookup("one").asInstanceOf[TLeafI]
    val two = lookup("two").asInstanceOf[TLeafF]
    val three = lookup("three").asInstanceOf[TLeafC]

    (one.getValue(0), two.getValue(0), three.getValue(0)) should be ((1, 1.1F, "uno"))
    (one.getValue(1), two.getValue(1), three.getValue(1)) should be ((2, 2.2F, "dos"))
    (one.getValue(2), two.getValue(2), three.getValue(2)) should be ((3, 3.3F, "tres"))
    (one.getValue(3), two.getValue(3), three.getValue(3)) should be ((4, 4.4F, "quatro"))
  }

  "RootTTree API" should "make accessors for custom case classes" in {
    case class VerySimple(x: Float, y: Float, z: Float)
    val verysimple = FreeHepRootTTreeReader[VerySimple]("src/test/resources/verysimple.root", "ntuple")
    verysimple.size should be (5)
    verysimple.get(0) should be (VerySimple(1.0F, 2.0F, 3.0F))
    verysimple.get(1) should be (VerySimple(4.0F, 5.0F, 6.0F))
    verysimple.get(2) should be (VerySimple(7.0F, 8.0F, 9.0F))
    verysimple.get(3) should be (VerySimple(10.0F, 11.0F, 12.0F))
    verysimple.get(4) should be (VerySimple(1.0F, 2.0F, 3.0F))

    case class VerySimple2(y: Float, x: Float)
    val verysimple2 = FreeHepRootTTreeReader[VerySimple2]("src/test/resources/verysimple.root", "ntuple")
    verysimple2.size should be (5)
    verysimple2.get(0) should be (VerySimple2(2.0F, 1.0F))
    verysimple2.get(1) should be (VerySimple2(5.0F, 4.0F))
    verysimple2.get(2) should be (VerySimple2(8.0F, 7.0F))
    verysimple2.get(3) should be (VerySimple2(11.0F, 10.0F))
    verysimple2.get(4) should be (VerySimple2(2.0F, 1.0F))

    case class Simple(one: Int, two: Float, three: String)
    val simple = FreeHepRootTTreeReader[Simple]("src/test/resources/simple.root", "tree")
    simple.size should be (4)
    simple.get(0) should be (Simple(1, 1.1F, "uno"))
    simple.get(1) should be (Simple(2, 2.2F, "dos"))
    simple.get(2) should be (Simple(3, 3.3F, "tres"))
    simple.get(3) should be (Simple(4, 4.4F, "quatro"))

    case class Simple2(three: String, one: Int)
    val simple2 = FreeHepRootTTreeReader[Simple2]("src/test/resources/simple.root", "tree")
    simple2.size should be (4)
    simple2.get(0) should be (Simple2("uno", 1))
    simple2.get(1) should be (Simple2("dos", 2))
    simple2.get(2) should be (Simple2("tres", 3))
    simple2.get(3) should be (Simple2("quatro", 4))

    case class BrokenSimple(one: Int, two: Float, three: String, four: Double)
    a [FreeHepException] should be thrownBy { FreeHepRootTTreeReader[BrokenSimple]("src/test/resources/simple.root", "tree") }

    case class BrokenSimple2(one: Int, two: Double, three: String)
    a [FreeHepException] should be thrownBy { FreeHepRootTTreeReader[BrokenSimple2]("src/test/resources/simple.root", "tree") }

    a [FreeHepException] should be thrownBy { FreeHepRootTTreeReader[Simple]("src/test/resources/simpleton.root", "tree") }
    a [FreeHepException] should be thrownBy { FreeHepRootTTreeReader[Simple]("src/test/resources/makeSimple.C", "tree") }
    a [FreeHepException] should be thrownBy { FreeHepRootTTreeReader[Simple]("src/test/resources/simple.root", "treety") }

    // TODO: get an example of the following that you can embed in src/test/resources
    // a [FreeHepException] should be thrownBy { FreeHepRootTTreeReader[Simple]("/opt/root/test/Event.root", "ProcessID0") }
    // a [FreeHepException] should be thrownBy { FreeHepRootTTreeReader[Simple]("/opt/root/test/Event.root", "hstat") }

  }
}
