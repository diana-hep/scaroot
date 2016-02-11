package org.dianahep.scaroot

import hep.io.root._
import hep.io.root.interfaces._

import org.dianahep.scaroot.api._

package object freehep {
  def ttreesInFile(rootFileLocation: String): Seq[String] = {
    val rootFileReader = try {
      new RootFileReader(rootFileLocation)
    }
    catch {
      case err: java.io.FileNotFoundException => throw new FreeHepException(s"""No file named "$rootFileLocation".""", Some(err))
      case err: java.io.IOException => throw new FreeHepException(s"""The file named "$rootFileLocation" is not a ROOT file.""", Some(err))
    }

    def rootInstanceOf(cls: RootClass, clsName: String): Boolean =
      if (cls.getClassName == clsName)
        true
      else if (cls.getSuperClasses.isEmpty)
        false
      else
        cls.getSuperClasses.exists(rootInstanceOf(_, clsName))

    def search(dir: TDirectory): List[String] =
      (0 until dir.nKeys).toList flatMap {i =>
        if (rootInstanceOf(dir.getKey(i).getObjectClass, "TTree"))
          List(dir.getKey(i).getName)
        else if (rootInstanceOf(dir.getKey(i).getObjectClass, "TDirectory"))
          search(dir.getKey(i).getObject.asInstanceOf[TDirectory]).map(dir.getKey(i).getName + "/" + _)
        else
          Nil
      }

    search(rootFileReader)
  }

  def leavesInTTree(rootFileLocation: String, ttreeLocation: String): Seq[(String, FieldType)] = {
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

    val leaves = ttree.getLeaves
    leaves.getLowerBound until leaves.getUpperBound map {i =>
      val tleaf = leaves.getElementAt(i)
      val tleafName = tleaf.asInstanceOf[TNamed].getName
      val tleafFieldType = tleaf match {
        case _: TLeafB => FieldType.Byte
        case _: TLeafS => FieldType.Short
        case _: TLeafI => FieldType.Int
        case _: TLeafL => FieldType.Long
        case _: TLeafF => FieldType.Float
        case _: TLeafD => FieldType.Double
        case _: TLeafC => FieldType.String
      }
      tleafName -> tleafFieldType
    }
  }
}

package freehep {
  class FreeHepException(message: String, cause: Option[Throwable] = None) extends RootApiException(message, cause)

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
    private val leafIdentifiers = Array.fill(rowBuilder.nameTypes.size)(null.asInstanceOf[TLeaf])

    private val leaves = ttree.getLeaves
    leaves.getLowerBound until leaves.getUpperBound foreach {i =>
      val tleaf = leaves.getElementAt(i)
      val tleafName = tleaf.asInstanceOf[TNamed].getName

      nameToIndex.get(tleafName) match {
        case Some(index) =>
          // put the TLeaf reference in the array for RootTTreeRowBuilder to look up
          leafIdentifiers(index) = tleaf.asInstanceOf[TLeaf]

          // test casting to make sure that we'll be able to do it at runtime
          (nameToType(tleafName), tleaf) match {
            case (FieldType.Byte, _: TLeafB) =>
            case (FieldType.Short, _: TLeafS) =>
            case (FieldType.Int, _: TLeafI) =>
            case (FieldType.Long, _: TLeafL) =>
            case (FieldType.Float, _: TLeafF) =>
            case (FieldType.Double, _: TLeafD) =>
            case (FieldType.String, _: TLeafC) =>
            case _ =>
              throw new FreeHepException(s"""The TTree named "$ttreeLocation" in file "$rootFileLocation" has leaf "$tleafName" with type ${tleaf.getClass.getName}, but expecting ${nameToType(tleafName)}.""", None)
          }

        case None =>
      }
    }
    val missing = leafIdentifiers.zipWithIndex collect {case (null, i) => rowBuilder.nameTypes(i)._1}
    if (!missing.isEmpty)
        throw new FreeHepException(s"""The TTree named "$ttreeLocation" in file "$rootFileLocation" has no leaves corresponding to the following fields: ${missing.map("\"" + _ + "\"").mkString(" ")}.""")

    // casts are fast and guaranteed by the above (as long as nobody gets access to our private (closed-over) rowBuilder
    def getId(index: Int) = leafIdentifiers(index)
    def setupToGetRow(row: Long) {
      if (row < 0  ||  row > size)
        throw new FreeHepException(s"""The TTree named "$ttreeLocation" in file "$rootFileLocation" only has $size rows; cannot get $row.""")
    }
    def getValueLeafB(leaf: TLeaf, row: Long): Byte = leaf.asInstanceOf[TLeafB].getValue(row)
    def getValueLeafS(leaf: TLeaf, row: Long): Short = leaf.asInstanceOf[TLeafS].getValue(row)
    def getValueLeafI(leaf: TLeaf, row: Long): Int = leaf.asInstanceOf[TLeafI].getValue(row)
    def getValueLeafL(leaf: TLeaf, row: Long): Long = leaf.asInstanceOf[TLeafL].getValue(row)
    def getValueLeafF(leaf: TLeaf, row: Long): Float = leaf.asInstanceOf[TLeafF].getValue(row)
    def getValueLeafD(leaf: TLeaf, row: Long): Double = leaf.asInstanceOf[TLeafD].getValue(row)
    def getValueLeafC(leaf: TLeaf, row: Long): String = leaf.asInstanceOf[TLeafC].getValue(row)

    def released = false
    def release() { }
  }
  object FreeHepRootTTreeReader {
    def apply[CASE : RootTTreeRowBuilder](rootFileLocation: String, ttreeLocation: String) =
      new FreeHepRootTTreeReader[CASE](rootFileLocation, ttreeLocation, implicitly[RootTTreeRowBuilder[CASE]])
  }

  class FreeHepRootTTreeIterator[CASE](val rootTTreeReader: RootTTreeReader[CASE, TLeaf]) extends RootTTreeIterator[CASE, TLeaf]
  object FreeHepRootTTreeIterator {
    def apply[CASE](rootTTreeReader: FreeHepRootTTreeReader[CASE]) =
      new FreeHepRootTTreeIterator[CASE](rootTTreeReader)
    def apply[CASE : RootTTreeRowBuilder](rootFileLocation: String, ttreeLocation: String) =
      new FreeHepRootTTreeIterator[CASE](new FreeHepRootTTreeReader[CASE](rootFileLocation, ttreeLocation, implicitly[RootTTreeRowBuilder[CASE]]))
  }
}
