package org.dianahep.scaroot

import hep.io.root._
import hep.io.root.interfaces._

import org.dianahep.scaroot.api._

package freehep {
  class FreeHepRootTTreeReader[CASE](val rootFileLocation: String,
                                     val ttreeLocation: String,
                                     rowBuilder: RootTTreeRowBuilder[CASE]) extends
                          RootTTreeReader[CASE, TLeaf](rowBuilder: RootTTreeRowBuilder[CASE]) {

    val rootFileReader = new RootFileReader(rootFileLocation)
    val ttree = rootFileReader.get(ttreeLocation).asInstanceOf[TTree]
    val size = ttree.getEntries

    private val nameToIndex = rowBuilder.nameTypes.map(_._1).zipWithIndex.toMap
    private val leaves = ttree.getLeaves
    leaves.getLowerBound until leaves.getUpperBound foreach {i =>
      val element = leaves.getElementAt(i)
      nameToIndex.get(element.asInstanceOf[TNamed].getName) match {
        case Some(index) => rowBuilder.leafIdentifiers(index) = element.asInstanceOf[TLeaf]
        case None =>
      }
    }
    if (rowBuilder.leafIdentifiers.exists(_ == null))
      throw new Exception

    def getValueLeafB(leaf: TLeaf, row: Int): Byte = leaf.asInstanceOf[TLeafB].getValue(row)
    def getValueLeafS(leaf: TLeaf, row: Int): Short = leaf.asInstanceOf[TLeafS].getValue(row)
    def getValueLeafI(leaf: TLeaf, row: Int): Int = leaf.asInstanceOf[TLeafI].getValue(row)
    def getValueLeafL(leaf: TLeaf, row: Int): Long = leaf.asInstanceOf[TLeafL].getValue(row)
    def getValueLeafF(leaf: TLeaf, row: Int): Float = leaf.asInstanceOf[TLeafF].getValue(row)
    def getValueLeafD(leaf: TLeaf, row: Int): Double = leaf.asInstanceOf[TLeafD].getValue(row)
    def getValueLeafC(leaf: TLeaf, row: Int): String = leaf.asInstanceOf[TLeafC].getValue(row)
  }
  object FreeHepRootTTreeReader {
    def apply[CASE : RootTTreeRowBuilder](rootFileLocation: String, ttreeLocation: String) =
      new FreeHepRootTTreeReader[CASE](rootFileLocation, ttreeLocation, implicitly[RootTTreeRowBuilder[CASE]])
  }
}
