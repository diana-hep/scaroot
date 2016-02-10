package org.dianahep.scaroot

import scala.language.implicitConversions

import com.sun.jna._

import org.dianahep.scaroot.api._

package native {
  class NativeException(message: String, cause: Option[Throwable] = None) extends RootApiException(message, cause)

  private[scaroot] object NativeROOT extends Library {
    Native.register("/resources/native/nativeRoot.so")

    @native def new_TFile(rootFileLocation: String): Long
    @native def close_TFile(tfile: Long): Unit
    @native def delete_TFile(tfile: Long): Unit
    @native def getTTree(tfile: Long, ttreeLocation: String): Long

    @native def ttreeGetNumEntries(ttree: Long): Long
    @native def ttreeGetNumLeaves(ttree: Long): Long
    @native def ttreeGetLeaf(ttree: Long, i: Long): Long
    @native def ttreeGetLeafName(ttree: Long, i: Long): String
    @native def ttreeGetLeafType(ttree: Long, i: Long): String

    @native def getValueLeafB(leaf: Long, row: Long): Byte
    @native def getValueLeafS(leaf: Long, row: Long): Short
    @native def getValueLeafI(leaf: Long, row: Long): Int
    @native def getValueLeafL(leaf: Long, row: Long): Long
    @native def getValueLeafF(leaf: Long, row: Long): Float
    @native def getValueLeafD(leaf: Long, row: Long): Double
    @native def getValueLeafC(leaf: Long, row: Long): String
  }

  case class Pointer(value: Long) extends AnyVal {   // AnyVal is lightweight: no objects are made at runtime
    def isEmpty = (value == 0L)
    def get = if (isEmpty) throw new NativeException("Attempt to use nullptr.") else value
    def getOption = if (isEmpty) None else Some(value)
    override def toString() = if (isEmpty) "nullptr" else value.toHexString // f"0x$value%016x"
  }
  // attempts to call a NativeROOT function with a nullptr argument will raise a JVM exception BEFORE calling the function
}

package object native {
  val nullptr = Pointer(0L)
  implicit def pointerToLong(p: Pointer) = p.get
  implicit def longToPointer(l: Long) = Pointer(l)
}

package native {
  class NativeRootTTreeReader[CASE](val rootFileLocation: String,
                                    val ttreeLocation: String,
                                    rowBuilder: RootTTreeRowBuilder[CASE]) extends
                          RootTTreeReader[CASE, Pointer](rowBuilder: RootTTreeRowBuilder[CASE]) {

    private var tfile: Pointer = NativeROOT.new_TFile(rootFileLocation)
    private def release_tfile() {
      NativeROOT.close_TFile(tfile)
      NativeROOT.delete_TFile(tfile)  // we own this pointer; have to delete it
      tfile = nullptr
    }

    private var ttree: Pointer = NativeROOT.getTTree(tfile, ttreeLocation)
    private def release_ttree() {
      ttree = nullptr  // we don't own this pointer; just forget its value
    }

    val size = NativeROOT.ttreeGetNumEntries(ttree)

    private val nameToIndex = rowBuilder.nameTypes.map(_._1).zipWithIndex.toMap
    private val nameToType = rowBuilder.nameTypes.toMap

    0L until NativeROOT.ttreeGetNumLeaves(ttree) foreach {i =>
      val tleaf: Pointer = NativeROOT.ttreeGetLeaf(ttree, i)
      val tleafName = NativeROOT.ttreeGetLeafName(ttree, i)
      val tleafType = NativeROOT.ttreeGetLeafType(ttree, i)

      nameToIndex.get(tleafName) match {
        case Some(index) =>
          // put the TLeaf pointer in the array for RootTTreeRowBuilder to look up
          rowBuilder.leafIdentifiers(index) = tleaf

          // verify that the leaf type matches the expected type
          (nameToType, tleafType) match {
            case (FieldType.Byte, "TLeafB") =>
            case (FieldType.Short, "TLeafS") =>
            case (FieldType.Int, "TLeafI") =>
            case (FieldType.Long, "TLeafL") =>
            case (FieldType.Float, "TLeafF") =>
            case (FieldType.Double, "TLeafD") =>
            case (FieldType.String, "TLeafC") =>
            case _ =>
              throw new NativeException(s"""The TTree named "$ttreeLocation" in file "$rootFileLocation" has leaf "$tleafName" with type ${tleaf.getClass.getName}, but expecting ${nameToType(tleafName)}.""", None)
          }

        case None =>
      }
    }

    val missing = rowBuilder.leafIdentifiers.zipWithIndex collect {case (null, i) => rowBuilder.nameTypes(i)._1}
    if (!missing.isEmpty)
        throw new NativeException(s"""The TTree named "$ttreeLocation" in file "$rootFileLocation" has no leaves corresponding to the following fields: ${missing.map("\"" + _ + "\"").mkString(" ")}.""")

    // casts are fast and guaranteed by the above (as long as nobody gets access to our private (closed-over) rowBuilder
    def getValueLeafB(leaf: Pointer, row: Long): Byte = NativeROOT.getValueLeafB(leaf, row)
    def getValueLeafS(leaf: Pointer, row: Long): Short = NativeROOT.getValueLeafS(leaf, row)
    def getValueLeafI(leaf: Pointer, row: Long): Int = NativeROOT.getValueLeafI(leaf, row)
    def getValueLeafL(leaf: Pointer, row: Long): Long = NativeROOT.getValueLeafL(leaf, row)
    def getValueLeafF(leaf: Pointer, row: Long): Float = NativeROOT.getValueLeafF(leaf, row)
    def getValueLeafD(leaf: Pointer, row: Long): Double = NativeROOT.getValueLeafD(leaf, row)
    def getValueLeafC(leaf: Pointer, row: Long): String = NativeROOT.getValueLeafC(leaf, row)

    def isOpen = !tfile.isEmpty

    def close() {
      // "good" interfaces will call this explicitly (e.g. RootTTreeIterator)
      release_tfile()
      release_ttree()
    }

    override def finalize() {
      // JVM isn't guaranteed to call this, but (only?) in cases of JVM shutdown, where it doesn't matter...
      if (isOpen) close()
    }
  }
  object NativeRootTTreeReader {
    def apply[CASE : RootTTreeRowBuilder](rootFileLocation: String, ttreeLocation: String) =
      new NativeRootTTreeReader[CASE](rootFileLocation, ttreeLocation, implicitly[RootTTreeRowBuilder[CASE]])
  }
}
