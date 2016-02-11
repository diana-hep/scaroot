package org.dianahep.scaroot

import scala.language.implicitConversions

import com.sun.jna._

import org.dianahep.scaroot.api._

package native {
  class NativeRootException(message: String, cause: Option[Throwable] = None) extends RootApiException(message, cause)

  private[scaroot] object NativeRoot extends Library {
    Native.register("/resources/native/nativeRoot.so")

    @native def new_TFile(rootFileLocation: String): Long
    @native def close_TFile(tfile: Long): Unit
    @native def delete_TFile(tfile: Long): Unit
    @native def getTTree(tfile: Long, ttreeLocation: String): Long

    @native def ttreeGetNumEntries(ttree: Long): Long
    @native def ttreeGetNumLeaves(ttree: Long): Long
    @native def ttreeGetLeaf(ttree: Long, i: Long): Long
    @native def ttreeGetLeafName(tleaf: Long): String
    @native def ttreeGetLeafType(tleaf: Long): String

    @native def new_dummy(ttree: Long, tleaf: Long): Long
    @native def delete_dummy(dummy: Long): Unit
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
    override def toString() = if (isEmpty) "nullptr" else value.toHexString // f"0x$value%016x"
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
    private def release_tfile() {
      NativeRoot.close_TFile(tfile)
      NativeRoot.delete_TFile(tfile)  // we own this pointer; have to delete it
      tfile = nullptr
    }

    private var ttree: Pointer = NativeRoot.getTTree(tfile, ttreeLocation)
    private def release_ttree() {
      ttree = nullptr  // we don't own this pointer; just forget its value
    }

    val size = NativeRoot.ttreeGetNumEntries(ttree)

    private val nameToIndex = rowBuilder.nameTypes.map(_._1).zipWithIndex.toMap
    private val nameToType = rowBuilder.nameTypes.toMap
    private val leafIdentifiers = Array.fill(rowBuilder.nameTypes.size)(nullptr)

    0L until NativeRoot.ttreeGetNumLeaves(ttree) foreach {i =>
      val tleaf: Pointer = NativeRoot.ttreeGetLeaf(ttree, i)
      val tleafName = NativeRoot.ttreeGetLeafName(tleaf)
      val tleafType = NativeRoot.ttreeGetLeafType(tleaf)

      nameToIndex.get(tleafName) match {
        case Some(index) =>
          // put the TLeaf pointer in the array for RootTTreeRowBuilder to look up
          leafIdentifiers(index) = NativeRoot.new_dummy(ttree, tleaf)

          // verify that the leaf type matches the expected type
          (nameToType(tleafName), tleafType) match {
            case (FieldType.Byte, "Int8_t") =>
            case (FieldType.Short, "Int16_t") =>
            case (FieldType.Int, "Int_t") =>
            case (FieldType.Long, "Int64_t") =>
            case (FieldType.Float, "Float_t") =>
            case (FieldType.Double, "Double_t") =>
            case (FieldType.String, "Char_t") =>
            case _ =>
              throw new NativeRootException(s"""The TTree named "$ttreeLocation" in file "$rootFileLocation" has leaf "$tleafName" with type $tleafType, but expecting ${nameToType(tleafName)}.""", None)
          }

        case None =>
      }
    }

    val missing = leafIdentifiers.zipWithIndex collect {case (`nullptr`, i) => rowBuilder.nameTypes(i)._1}
    if (!missing.isEmpty)
        throw new NativeRootException(s"""The TTree named "$ttreeLocation" in file "$rootFileLocation" has no leaves corresponding to the following fields: ${missing.map("\"" + _ + "\"").mkString(" ")}.""")

    def release_dummies() {
      leafIdentifiers.foreach(NativeRoot.delete_dummy(_))
    }

    // casts are fast and guaranteed by the above (as long as nobody gets access to our private (closed-over) rowBuilder
    def getId(index: Int) = leafIdentifiers(index)
    def getRow(row: Long) {
      if (NativeRoot.ttreeGetRow(ttree, row) == 0)
        throw new Exception
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
