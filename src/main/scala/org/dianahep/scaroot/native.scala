package org.dianahep.scaroot

import scala.language.implicitConversions

import com.sun.jna._

import org.dianahep.scaroot.api._

package object native {
  def ttreesInFile(rootFileLocation: String): Seq[String] = {
    import Pointer._

    val tfile: Pointer = NativeRoot.new_TFile(rootFileLocation)
    if (NativeRoot.tfileIsOpen(tfile) == 0)
      throw new NativeRootException(s"""No file named "$rootFileLocation".""", None)
    if (NativeRoot.tfileIsZombie(tfile) != 0)
      throw new NativeRootException(s"""The file named "$rootFileLocation" is not a ROOT file.""", None)

    def search(tdir: Long): List[String] =
      (0L until NativeRoot.tdirNumKeys(tdir)).toList flatMap {i =>
        if (NativeRoot.tdirKeyIsTTree(tdir, i) != 0)
          List(NativeRoot.tdirKeyName(tdir, i))
        else if (NativeRoot.tdirKeyIsTDirectory(tdir, i) != 0) {
          val dirName = NativeRoot.tdirKeyName(tdir, i)
          search(NativeRoot.tdirKeyGet(tdir, i)).map(dirName + "/" + _)
        }
        else
          Nil
      }

    search(tfile)
  }

  private[native] def indexOfName(dir: Pointer, name: String): Option[Long] = {
    0L until NativeRoot.tdirNumKeys(dir) find {i =>
      NativeRoot.tdirKeyName(dir, i) == name
    }
  }

  private[native] def getPath(dir: Pointer, path: String): Pointer = {
    def search(d: Pointer, p: List[String]): Pointer = p match {
      case Nil => throw new NativeRootException(s"""Cannot resolve empty path "$path".""", None)
      case top :: Nil => NativeRoot.getTTree(d, top)
      case top :: rest => indexOfName(d, top) match {
        case Some(index) if (NativeRoot.tdirKeyIsTDirectory(d, index) != 0) =>
          val subdir = NativeRoot.tdirKeyGet(d, index)
          search(subdir, rest)
        case Some(index) =>
          throw new NativeRootException(s"""Path element in "$path" ("$top") is not a TDirectory.""", None)
        case None =>
          throw new NativeRootException(s"""Cannot find "$top" in "$path".""", None)
      }
    }
    search(dir, path.split("/").filter(!_.isEmpty).toList)
  }

  def leavesInTTree(rootFileLocation: String, ttreeLocation: String): Seq[(String, FieldType)] = {
    import Pointer._

    val tfile: Pointer = NativeRoot.new_TFile(rootFileLocation)
    if (NativeRoot.tfileIsOpen(tfile) == 0)
      throw new NativeRootException(s"""No file named "$rootFileLocation".""", None)
    if (NativeRoot.tfileIsZombie(tfile) != 0)
      throw new NativeRootException(s"""The file named "$rootFileLocation" is not a ROOT file.""", None)

    val ttree: Pointer = getPath(tfile, ttreeLocation)
    if (ttree == nullptr)
      throw new NativeRootException(s"""An error occurred when trying to read "$ttreeLocation" from file "$rootFileLocation".""", None)

    0L until NativeRoot.ttreeGetNumLeaves(ttree) map {i =>
      val tleaf = NativeRoot.ttreeGetLeaf(ttree, i)
      val tleafName = NativeRoot.ttreeGetLeafName(tleaf)
      val tleafType = NativeRoot.ttreeGetLeafType(tleaf)
      val tleafFieldType = tleafType match {
        case "Int8_t"   => FieldType.Byte
        case "Int16_t"  => FieldType.Short
        case "Int_t"    => FieldType.Int
        case "Int64_t"  => FieldType.Long
        case "Float_t"  => FieldType.Float
        case "Double_t" => FieldType.Double
        case "Char_t"   => FieldType.String
      }
      tleafName -> tleafFieldType
    }
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

    @native def tdirNumKeys(tdir: Long): Long
    @native def tdirKeyName(tdir: Long, index: Long): String
    @native def tdirKeyIsTTree(tdir: Long, index: Long): Byte
    @native def tdirKeyIsTDirectory(tdir: Long, index: Long): Byte
    @native def tdirKeyGet(tdir: Long, index: Long): Long
    @native def getTTree(tdir: Long, ttreeLocation: String): Long

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
    implicit def pointerToLong(p: Pointer): Long = p.get
    implicit def longToPointer(l: Long): Pointer = Pointer(l)
  }

  class NativeRootTTreeReader[CASE](val rootFileLocation: String,
                                    val ttreeLocation: String,
                                    rowBuilder: RootTTreeRowBuilder[CASE]) extends
                          RootTTreeReader[CASE, Pointer](rowBuilder: RootTTreeRowBuilder[CASE]) {
    import Pointer._

    private var tfile: Pointer = NativeRoot.new_TFile(rootFileLocation)
    if (NativeRoot.tfileIsOpen(tfile) == 0)
      throw new NativeRootException(s"""No file named "$rootFileLocation".""", None)
    if (NativeRoot.tfileIsZombie(tfile) != 0)
      throw new NativeRootException(s"""The file named "$rootFileLocation" is not a ROOT file.""", None)
    private def release_tfile() {
      // NativeRoot.delete_TFile(tfile)
      tfile = nullptr
    }

    private var ttree: Pointer = getPath(tfile, ttreeLocation)
    if (ttree == nullptr)
      throw new NativeRootException(s"""An error occurred when trying to read "$ttreeLocation" from file "$rootFileLocation".""", None)

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
            case (FieldType.Byte,   "Int8_t")   => leafDeleters(index) = {() => leafIdentifiers(index) = nullptr} // ; NativeRoot.delete_dummyB(dummy)}
            case (FieldType.Short,  "Int16_t")  => leafDeleters(index) = {() => leafIdentifiers(index) = nullptr} // ; NativeRoot.delete_dummyS(dummy)}
            case (FieldType.Int,    "Int_t")    => leafDeleters(index) = {() => leafIdentifiers(index) = nullptr} // ; NativeRoot.delete_dummyI(dummy)}
            case (FieldType.Long,   "Int64_t")  => leafDeleters(index) = {() => leafIdentifiers(index) = nullptr} // ; NativeRoot.delete_dummyL(dummy)}
            case (FieldType.Float,  "Float_t")  => leafDeleters(index) = {() => leafIdentifiers(index) = nullptr} // ; NativeRoot.delete_dummyF(dummy)}
            case (FieldType.Double, "Double_t") => leafDeleters(index) = {() => leafIdentifiers(index) = nullptr} // ; NativeRoot.delete_dummyD(dummy)}
            case (FieldType.String, "Char_t")   => leafDeleters(index) = {() => leafIdentifiers(index) = nullptr}
            case _ =>
              release_tfile()
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
    if (!missing.isEmpty)
      throw new NativeRootException(s"""The TTree named "$ttreeLocation" in file "$rootFileLocation" has no leaves corresponding to the following fields: ${missing.map("\"" + _ + "\"").mkString(" ")}.""")

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

    def released = tfile.isEmpty

    def release() {
      // "good" interfaces will call this explicitly (e.g. RootTTreeIterator)
      release_tfile()
      release_dummies()
    }

    override def finalize() {
      // JVM isn't guaranteed to call this, but (only?) in cases of JVM shutdown, where it doesn't matter...
      if (!released) release()
    }
  }
  object NativeRootTTreeReader {
    def apply[CASE : RootTTreeRowBuilder](rootFileLocation: String, ttreeLocation: String) =
      new NativeRootTTreeReader[CASE](rootFileLocation, ttreeLocation, implicitly[RootTTreeRowBuilder[CASE]])
  }

  class NativeRootTTreeIterator[CASE](val rootTTreeReader: RootTTreeReader[CASE, Pointer]) extends RootTTreeIterator[CASE, Pointer]
  object NativeRootTTreeIterator {
    def apply[CASE](rootTTreeReader: NativeRootTTreeReader[CASE]) =
      new NativeRootTTreeIterator[CASE](rootTTreeReader)
    def apply[CASE : RootTTreeRowBuilder](rootFileLocation: String, ttreeLocation: String) =
      new NativeRootTTreeIterator[CASE](new NativeRootTTreeReader[CASE](rootFileLocation, ttreeLocation, implicitly[RootTTreeRowBuilder[CASE]]))
  }
}
