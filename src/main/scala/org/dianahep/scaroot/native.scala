package org.dianahep.scaroot

import scala.language.implicitConversions

import com.sun.jna._

import org.dianahep.scaroot.api._

package object native {
  def rootFileListing(rootFileLocation: String): Seq[String] = {
    val tfile = NativeRoot.new_TFile(rootFileLocation)
    if (NativeRoot.tfileIsOpen(tfile) == 0) {
      NativeRoot.delete_TFile(tfile)
      throw new NativeRootException(s"""No file named "$rootFileLocation".""", None)
    }
    if (NativeRoot.tfileIsZombie(tfile) != 0) {
      NativeRoot.close_TFile(tfile)
      NativeRoot.delete_TFile(tfile)
      throw new NativeRootException(s"""The file named "$rootFileLocation" is not a ROOT file.""", None)
    }

    val out = 0L until NativeRoot.tfileNumKeys(tfile) map {index =>
      NativeRoot.tfileKeyName(tfile, index)
    }

    NativeRoot.close_TFile(tfile)
    NativeRoot.delete_TFile(tfile)
    out
  }
}

package native {
  class NativeRootException(message: String, cause: Option[Throwable] = None) extends RootApiException(message, cause)

  private[scaroot] object NativeRoot extends Library {
    Native.register("/resources/native/nativeRoot.so")

    @native def new_TFile(rootFileLocation: String): Long
    @native def close_TFile(tfile: Long): Unit
    @native def delete_TFile(tfile: Long): Unit
    @native def tfileIsOpen(tfile: Long): Byte
    @native def tfileIsZombie(tfile: Long): Byte
    @native def tfileNumKeys(tfile: Long): Long
    @native def tfileKeyName(tfile: Long, index: Long): String
    @native def getTTree(tfile: Long, ttreeLocation: String): Long

    @native def ttreeGetNumEntries(ttree: Long): Long
    @native def ttreeGetNumLeaves(ttree: Long): Long
    @native def ttreeGetLeaf(ttree: Long, i: Long): Long
    @native def ttreeGetLeafName(tleaf: Long): String
    @native def ttreeGetLeafType(tleaf: Long): String

    @native def new_dummy(ttree: Long, tleaf: Long): Long
    @native def delete_dummyB(dummy: Long): Unit
    @native def delete_dummyS(dummy: Long): Unit
    @native def delete_dummyI(dummy: Long): Unit
    @native def delete_dummyL(dummy: Long): Unit
    @native def delete_dummyF(dummy: Long): Unit
    @native def delete_dummyD(dummy: Long): Unit
    @native def ttreeGetRow(ttree: Long, row: Long): Byte

    @native def getValueLeafB(leaf: Long): Byte
    @native def getValueLeafS(leaf: Long): Short
    @native def getValueLeafI(leaf: Long): Int
    @native def getValueLeafL(leaf: Long): Long
    @native def getValueLeafF(leaf: Long): Float
    @native def getValueLeafD(leaf: Long): Double
    @native def getValueLeafC(leaf: Long): String
  }

  case class Pointer(value: Long) extends AnyVal {   // AnyVal is lightweight: no objects are made at runtime
    def isEmpty = (value == 0L)
    def get = if (isEmpty) throw new NativeRootException("Attempt to use nullptr.") else value
    def getOption = if (isEmpty) None else Some(value)
    override def toString() = if (isEmpty) "nullptr" else f"0x$value%016x"
  }
  // attempts to call a NativeRoot function with a nullptr argument will raise a JVM exception BEFORE calling the function
  object Pointer {
    val nullptr = Pointer(0L)
    implicit def pointerToLong(p: Pointer) = p.get
    implicit def longToPointer(l: Long) = Pointer(l)
  }

  class NativeRootTTreeReader[CASE](val rootFileLocation: String,
                                    val ttreeLocation: String,
                                    rowBuilder: RootTTreeRowBuilder[CASE]) extends
                          RootTTreeReader[CASE, Pointer](rowBuilder: RootTTreeRowBuilder[CASE]) {
    import Pointer._

    private var tfile: Pointer = NativeRoot.new_TFile(rootFileLocation)
    if (NativeRoot.tfileIsOpen(tfile) == 0) {
      NativeRoot.delete_TFile(tfile)
      throw new NativeRootException(s"""No file named "$rootFileLocation".""", None)
    }
    if (NativeRoot.tfileIsZombie(tfile) != 0) {
      NativeRoot.close_TFile(tfile)
      NativeRoot.delete_TFile(tfile)
      throw new NativeRootException(s"""The file named "$rootFileLocation" is not a ROOT file.""", None)
    }
    private def release_tfile() {
      NativeRoot.close_TFile(tfile)
      NativeRoot.delete_TFile(tfile)  // we own this pointer; have to delete it
      tfile = nullptr
    }

    private var ttree: Pointer = NativeRoot.getTTree(tfile, ttreeLocation)
    if (ttree == nullptr) {
      release_tfile()
      throw new NativeRootException(s"""An error occurred when trying to read "$ttreeLocation" from file "$rootFileLocation".""", None)
    }
    private def release_ttree() {
      ttree = nullptr  // we don't own this pointer; just forget its value
    }

    val size = NativeRoot.ttreeGetNumEntries(ttree)

    private val nameToIndex = rowBuilder.nameTypes.map(_._1).zipWithIndex.toMap
    private val nameToType = rowBuilder.nameTypes.toMap
    private val leafIdentifiers = Array.fill(rowBuilder.nameTypes.size)(nullptr)
    private val leafDeleters = Array.fill(rowBuilder.nameTypes.size)(() => ())

    0L until NativeRoot.ttreeGetNumLeaves(ttree) foreach {i =>
      val tleaf: Pointer = NativeRoot.ttreeGetLeaf(ttree, i)
      val tleafName = NativeRoot.ttreeGetLeafName(tleaf)
      val tleafType = NativeRoot.ttreeGetLeafType(tleaf)

      nameToIndex.get(tleafName) match {
        case Some(index) =>
          // put the TLeaf pointer in the array for RootTTreeRowBuilder to look up
          val dummy = NativeRoot.new_dummy(ttree, tleaf)
          leafIdentifiers(index) = dummy

          // verify that the leaf type matches the expected type
          (nameToType(tleafName), tleafType) match {
            case (FieldType.Byte,   "Int8_t")   => leafDeleters(index) = {() => NativeRoot.delete_dummyB(dummy); leafIdentifiers(index) = nullptr}
            case (FieldType.Short,  "Int16_t")  => leafDeleters(index) = {() => NativeRoot.delete_dummyS(dummy); leafIdentifiers(index) = nullptr}
            case (FieldType.Int,    "Int_t")    => leafDeleters(index) = {() => NativeRoot.delete_dummyI(dummy); leafIdentifiers(index) = nullptr}
            case (FieldType.Long,   "Int64_t")  => leafDeleters(index) = {() => NativeRoot.delete_dummyL(dummy); leafIdentifiers(index) = nullptr}
            case (FieldType.Float,  "Float_t")  => leafDeleters(index) = {() => NativeRoot.delete_dummyF(dummy); leafIdentifiers(index) = nullptr}
            case (FieldType.Double, "Double_t") => leafDeleters(index) = {() => NativeRoot.delete_dummyD(dummy); leafIdentifiers(index) = nullptr}
            case (FieldType.String, "Char_t")   => leafDeleters(index) = {() => leafIdentifiers(index) = nullptr}
            case _ =>
              release_tfile()
              release_ttree()
              leafDeleters.foreach(f => f())
              throw new NativeRootException(s"""The TTree named "$ttreeLocation" in file "$rootFileLocation" has leaf "$tleafName" with type $tleafType, but expecting ${nameToType(tleafName)}.""", None)
          }

        case None =>
      }
    }

    def release_dummies() {
      leafDeleters.foreach(f => f())
    }

    val missing = leafIdentifiers.zipWithIndex collect {case (`nullptr`, i) => rowBuilder.nameTypes(i)._1}
    if (!missing.isEmpty) {
      release_tfile()
      release_ttree()
      release_dummies()
      throw new NativeRootException(s"""The TTree named "$ttreeLocation" in file "$rootFileLocation" has no leaves corresponding to the following fields: ${missing.map("\"" + _ + "\"").mkString(" ")}.""")
    }

    // casts are fast and guaranteed by the above (as long as nobody gets access to our private (closed-over) rowBuilder
    def getId(index: Int) = leafIdentifiers(index)
    def setupToGetRow(row: Long) {
      if (row < 0  ||  row > size)
        throw new NativeRootException(s"""The TTree named "$ttreeLocation" in file "$rootFileLocation" only has $size rows; cannot get $row.""")
      if (NativeRoot.ttreeGetRow(ttree, row) == 0)
        throw new NativeRootException(s"""Failed to get entry $row from TTree named "$ttreeLocation" in file "$rootFileLocation".""")
    }
    def getValueLeafB(leaf: Pointer, row: Long): Byte = NativeRoot.getValueLeafB(leaf)
    def getValueLeafS(leaf: Pointer, row: Long): Short = NativeRoot.getValueLeafS(leaf)
    def getValueLeafI(leaf: Pointer, row: Long): Int = NativeRoot.getValueLeafI(leaf)
    def getValueLeafL(leaf: Pointer, row: Long): Long = NativeRoot.getValueLeafL(leaf)
    def getValueLeafF(leaf: Pointer, row: Long): Float = NativeRoot.getValueLeafF(leaf)
    def getValueLeafD(leaf: Pointer, row: Long): Double = NativeRoot.getValueLeafD(leaf)
    def getValueLeafC(leaf: Pointer, row: Long): String = NativeRoot.getValueLeafC(leaf)

    def isOpen = !tfile.isEmpty

    def close() {
      // "good" interfaces will call this explicitly (e.g. RootTTreeIterator)
      release_tfile()
      release_ttree()
      release_dummies()
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
