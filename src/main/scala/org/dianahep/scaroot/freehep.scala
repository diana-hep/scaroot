package org.dianahep.scaroot

import hep.io.root._
import hep.io.root.interfaces._

import org.dianahep.scaroot.api._

package freehep {
  class FreeHepException(message: String, cause: Option[Throwable] = None) extends RuntimeException(message, cause.getOrElse(null))

  class FreeHepRootTTreeReader[CASE](val rootFileLocation: String,
                                     val ttreeLocation: String,
                                     rowBuilder: RootTTreeRowBuilder[CASE]) extends
                          RootTTreeReader[CASE, TLeaf](rowBuilder: RootTTreeRowBuilder[CASE]) {

    val rootFileReader = try {
      new RootFileReader(rootFileLocation)
    }
    catch {
      case err: java.io.FileNotFoundException => throw new FreeHepException(s"""No file named "$rootFileLocation".""", Some(err))
      case err: java.io.IOException => throw new FreeHepException(s"""The file named "$rootFileLocation" is not a ROOT file.""", Some(err))
    }

    val ttree = try {
      rootFileReader.get(ttreeLocation).asInstanceOf[TTree]
    }
    catch {
      case err: java.lang.ClassCastException => throw new FreeHepException(s"""The object named "$ttreeLocation" in file "$rootFileLocation" is not a TTree.""", Some(err))
      case err: java.lang.RuntimeException => throw new FreeHepException(s"""No object named "$ttreeLocation" in file "$rootFileLocation".""", Some(err))
      case err: java.io.IOException => throw new FreeHepException(s"""An error occurred when trying to read "$ttreeLocation" from file "$rootFileLocation" (see "Cause:" in the stack trace for details).""", Some(err))
    }

    val size = ttree.getEntries

    private val nameToIndex = rowBuilder.nameTypes.map(_._1).zipWithIndex.toMap
    private val nameToType = rowBuilder.nameTypes.toMap
    private val leaves = ttree.getLeaves
    leaves.getLowerBound until leaves.getUpperBound foreach {i =>
      val element = leaves.getElementAt(i)
      val name = element.asInstanceOf[TNamed].getName

      nameToIndex.get(name) match {
        case Some(index) =>
          // put the TLeaf reference in the array for RootTTreeRowBuilder to look up
          rowBuilder.leafIdentifiers(index) = element

          // test casting to make sure that we'll be able to do it at runtime
          try {
            nameToType(name) match {
              case FieldType.Byte => element.asInstanceOf[TLeafB]
              case FieldType.Short => element.asInstanceOf[TLeafS]
              case FieldType.Int => element.asInstanceOf[TLeafI]
              case FieldType.Long => element.asInstanceOf[TLeafL]
              case FieldType.Float => element.asInstanceOf[TLeafF]
              case FieldType.Double => element.asInstanceOf[TLeafD]
              case FieldType.String => element.asInstanceOf[TLeafC]
            }
          }
          catch {
            case err: java.lang.ClassCastException => throw new FreeHepException(s"""The TTree named "$ttreeLocation" in file "$rootFileLocation" has leaf "$name" with type ${element.getClass.getName}, but expecting ${nameToType(name)}.""", Some(err))
          }

        case None =>
      }
    }
    val missing = rowBuilder.leafIdentifiers.zipWithIndex collect {case (null, i) => rowBuilder.nameTypes(i)._1}
    if (!missing.isEmpty)
        throw new FreeHepException(s"""The TTree named "$ttreeLocation" in file "$rootFileLocation" has no leaves corresponding to the following fields: ${missing.map("\"" + _ + "\"").mkString(" ")}.""")

    // casts are fast and guaranteed by the above (as long as nobody gets access to our private (closed-over) rowBuilder
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
