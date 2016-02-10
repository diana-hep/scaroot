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
    case class Something(x: Double, y: Double, z: String)
    val rootTTree = RootTTreeReader[Something]("src/test/resources/verysimple.root", "ntuple")
    println(rootTTree.get(0))
  }
}
