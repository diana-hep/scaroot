package org.dianahep.scaroot

import hep.io.root._
import hep.io.root.interfaces._

import org.dianahep.scaroot.api._

package freehep {
  class FreeHepRootTTreeReader[T](val rootFileLocation: String, val ttreeLocation: String, rowBuilder: RootTTreeRowBuilder[T]) extends RootTTreeReader[T](rowBuilder: RootTTreeRowBuilder[T]) {
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

    def getValueLeafB(leaf: AnyRef, row: Int): Byte = leaf.asInstanceOf[TLeafB].getValue(row)
    def getValueLeafS(leaf: AnyRef, row: Int): Short = leaf.asInstanceOf[TLeafS].getValue(row)
    def getValueLeafI(leaf: AnyRef, row: Int): Int = leaf.asInstanceOf[TLeafI].getValue(row)
    def getValueLeafL(leaf: AnyRef, row: Int): Long = leaf.asInstanceOf[TLeafL].getValue(row)
    def getValueLeafF(leaf: AnyRef, row: Int): Float = leaf.asInstanceOf[TLeafF].getValue(row)
    def getValueLeafD(leaf: AnyRef, row: Int): Double = leaf.asInstanceOf[TLeafD].getValue(row)
    def getValueLeafC(leaf: AnyRef, row: Int): String = leaf.asInstanceOf[TLeafC].getValue(row)
  }
  object FreeHepRootTTreeReader {
    def apply[T : RootTTreeRowBuilder](rootFileLocation: String, ttreeLocation: String) =
      new FreeHepRootTTreeReader(rootFileLocation, ttreeLocation, implicitly[RootTTreeRowBuilder[T]])
  }
}
