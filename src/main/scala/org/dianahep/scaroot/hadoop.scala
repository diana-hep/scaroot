package org.dianahep.scaroot

import scala.collection.JavaConversions._
import scala.language.experimental.macros 
import scala.reflect._
// import scala.reflect.macros.blackbox.Context
import scala.reflect.macros.Context

import org.apache.hadoop.fs.FileStatus
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.compress.CompressionCodecFactory
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.io.Writable
import org.apache.hadoop.mapreduce.InputSplit
import org.apache.hadoop.mapreduce.JobContext
import org.apache.hadoop.mapreduce.JobStatus
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.input.FileSplit
import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.RecordReader
import org.apache.hadoop.mapreduce.TaskAttemptContext

import org.dianahep.scaroot.freehep.FreeHepRootTTreeReader
import org.dianahep.scaroot.native.NativeRootTTreeReader
import org.dianahep.scaroot.api.RootTTreeRowBuilder

package hadoop {  
  case class KeyWritable(ttreeEntry: Long) extends LongWritable(ttreeEntry)

  trait ValueSerializer[CASE] {
    def read(in: java.io.DataInput): CASE
    def write(out: java.io.DataOutput, obj: CASE)
  }
  object ValueSerializer {
    implicit def compileValueSerializer[CASE]: ValueSerializer[CASE] = macro compileValueSerializerImpl[CASE]

    // Commented-out parts are for Scala 2.11, replacements are for Scala 2.10.
    // See api.scala for details.

    def compileValueSerializerImpl[CASE : c.WeakTypeTag](c: Context): c.Expr[ValueSerializer[CASE]] = {
      import c.universe._
      val caseType = weakTypeOf[CASE]

      // val fields = caseType.decls.collectFirst {
      //   case m: MethodSymbol if (m.isPrimaryConstructor) => m
      // }.get.paramLists.head
      val fields = caseType.declarations.collectFirst {
        case m: MethodSymbol if (m.isPrimaryConstructor) => m
      }.get.paramss.head

      val (readParams, writeStatements) = fields.map {field =>
        val fieldName = field.asTerm.name
        // val NullaryMethodType(fieldType) = caseType.decl(fieldName).typeSignature
        val NullaryMethodType(fieldType) = caseType.declaration(fieldName).typeSignature

        // if (fieldType =:= typeOf[Boolean])
        //   (q"in.readBoolean()", q"out.writeBoolean(obj.$fieldName)")
        // else if (fieldType =:= typeOf[Byte])
        //   (q"in.readByte()", q"out.writeByte(obj.$fieldName)")
        // else if (fieldType =:= typeOf[Char])
        //   (q"in.readChar()", q"out.writeChar(obj.$fieldName)")
        // else if (fieldType =:= typeOf[Double])
        //   (q"in.readDouble()", q"out.writeDouble(obj.$fieldName)")
        // else if (fieldType =:= typeOf[Float])
        //   (q"in.readFloat()", q"out.writeFloat(obj.$fieldName)")
        // else if (fieldType =:= typeOf[Int])
        //   (q"in.readInt()", q"out.writeInt(obj.$fieldName)")
        // else if (fieldType =:= typeOf[Long])
        //   (q"in.readLong()", q"out.writeLong(obj.$fieldName)")
        // else if (fieldType =:= typeOf[Short])
        //   (q"in.readShort()", q"out.writeShort(obj.$fieldName)")
        // else if (fieldType =:= typeOf[String])
        //   (q"in.readUTF()", q"out.writeUTF(obj.$fieldName)")
        // else
        //   throw new NotImplementedError(s"no handler for type $fieldType")

        if (fieldType =:= typeOf[Boolean])
          (Apply(Select(Ident(newTermName("in")), newTermName("readBoolean")), List()),
            Apply(Select(Ident(newTermName("out")), newTermName("writeBoolean")), List(Select(Ident(newTermName("obj")), fieldName))))
        else if (fieldType =:= typeOf[Byte])
          (Apply(Select(Ident(newTermName("in")), newTermName("readByte")), List()),
            Apply(Select(Ident(newTermName("out")), newTermName("writeByte")), List(Select(Ident(newTermName("obj")), fieldName))))
        else if (fieldType =:= typeOf[Char])
          (Apply(Select(Ident(newTermName("in")), newTermName("readChar")), List()),
            Apply(Select(Ident(newTermName("out")), newTermName("writeChar")), List(Select(Ident(newTermName("obj")), fieldName))))
        else if (fieldType =:= typeOf[Double])
          (Apply(Select(Ident(newTermName("in")), newTermName("readDouble")), List()),
            Apply(Select(Ident(newTermName("out")), newTermName("writeDouble")), List(Select(Ident(newTermName("obj")), fieldName))))
        else if (fieldType =:= typeOf[Float])
          (Apply(Select(Ident(newTermName("in")), newTermName("readFloat")), List()),
            Apply(Select(Ident(newTermName("out")), newTermName("writeFloat")), List(Select(Ident(newTermName("obj")), fieldName))))
        else if (fieldType =:= typeOf[Int])
          (Apply(Select(Ident(newTermName("in")), newTermName("readInt")), List()),
            Apply(Select(Ident(newTermName("out")), newTermName("writeInt")), List(Select(Ident(newTermName("obj")), fieldName))))
        else if (fieldType =:= typeOf[Long])
          (Apply(Select(Ident(newTermName("in")), newTermName("readLong")), List()),
            Apply(Select(Ident(newTermName("out")), newTermName("writeLong")), List(Select(Ident(newTermName("obj")), fieldName))))
        else if (fieldType =:= typeOf[Short])
          (Apply(Select(Ident(newTermName("in")), newTermName("readShort")), List()),
            Apply(Select(Ident(newTermName("out")), newTermName("writeShort")), List(Select(Ident(newTermName("obj")), fieldName))))
        else if (fieldType =:= typeOf[String])
          (Apply(Select(Ident(newTermName("in")), newTermName("readUTF")), List()),
            Apply(Select(Ident(newTermName("out")), newTermName("writeUTF")), List(Select(Ident(newTermName("obj")), fieldName))))
        else
          throw new NotImplementedError(s"no handler for type $fieldType")

      }.unzip

      // c.Expr[ValueSerializer[CASE]](q"""
      //   import org.dianahep.scaroot.hadoop._
      //   new ValueSerializer[$caseType] {
      //     def read(in: java.io.DataInput) = new $caseType(..$readParams)
      //     def write(out: java.io.DataOutput, obj: $caseType) { ..$writeStatements }
      //   }
      // """)
      import Flag.FINAL
      import Flag.PARAM
      c.Expr[ValueSerializer[CASE]](Block(List(Import(Select(Select(Select(Ident(newTermName("org")), newTermName("dianahep")), newTermName("scaroot")), newTermName("hadoop")), List(ImportSelector(nme.WILDCARD, 44, null, -1)))), Block(List(ClassDef(Modifiers(FINAL), newTypeName("$anon"), List(), Template(List(AppliedTypeTree(Ident(newTypeName("ValueSerializer")), List(Ident(newTypeName("caseType"))))), emptyValDef, List(DefDef(Modifiers(), nme.CONSTRUCTOR, List(), List(List()), TypeTree(), Block(List(Apply(Select(Super(This(tpnme.EMPTY), tpnme.EMPTY), nme.CONSTRUCTOR), List())), Literal(Constant(())))), DefDef(Modifiers(), newTermName("read"), List(), List(List(ValDef(Modifiers(PARAM), newTermName("in"), Select(Select(Ident(newTermName("java")), newTermName("io")), newTypeName("DataInput")), EmptyTree))), TypeTree(), Apply(Select(New(Ident(newTypeName("caseType"))), nme.CONSTRUCTOR), List(Ident(newTermName("readParams"))))), DefDef(Modifiers(), newTermName("write"), List(), List(List(ValDef(Modifiers(PARAM), newTermName("out"), Select(Select(Ident(newTermName("java")), newTermName("io")), newTypeName("DataOutput")), EmptyTree), ValDef(Modifiers(PARAM), newTermName("obj"), Ident(newTypeName("caseType")), EmptyTree))), Select(Ident(newTermName("scala")), newTypeName("Unit")), Ident(newTermName("writeStatements"))))))), Apply(Select(New(Ident(newTypeName("$anon"))), nme.CONSTRUCTOR), List()))))
    }
  }

  abstract class ValueWritable[CASE : ValueSerializer] extends Writable {
    private var wrapped = null.asInstanceOf[CASE]
    def isEmpty = (wrapped == null)
    def get =
      if (isEmpty) throw new java.util.NoSuchElementException("ValueWritable does not contain any data (instantiated but not deserialized).")
      else wrapped
    def put(x: CASE) {
      wrapped = x
    }
    def readFields(in: java.io.DataInput) {
      wrapped = implicitly[ValueSerializer[CASE]].read(in)
    }
    def write(out: java.io.DataOutput) {
      implicitly[ValueSerializer[CASE]].write(out, get)
    }
    override def toString() =
      if (isEmpty)
        "ValueWritable()"
      else
        "ValueWritable(" + get.toString + ")"
  }
  object ValueWritable {
    def unapply[CASE](x: ValueWritable[CASE]): Option[CASE] =
      if (x.isEmpty)
        None
      else
        Some(x.get)
  }

  abstract class RootInputFormat[CASE : RootTTreeRowBuilder : ValueSerializer, WRITABLE <: ValueWritable[CASE] : ClassTag](ttreeLocation: String) extends FileInputFormat[KeyWritable, WRITABLE] {
    override def createRecordReader(split: InputSplit, context: TaskAttemptContext): RecordReader[KeyWritable, WRITABLE] =
      new RootRecordReader

    override def isSplitable(context: JobContext, file: Path): Boolean = false

    override def getSplits(job: JobContext): java.util.List[InputSplit] =
      super.getSplits(job)  // does the right thing; this is here as a reminder that it's overridable

    class RootRecordReader extends RecordReader[KeyWritable, WRITABLE] {
      private var reader: FreeHepRootTTreeReader[CASE] = null
      private var row = -1L
      private var key = null.asInstanceOf[KeyWritable]
      private var value = null.asInstanceOf[WRITABLE]
      private val valueConstructor = classTag[WRITABLE].runtimeClass.getConstructor()
      valueConstructor.setAccessible(true)

      override def initialize(split: InputSplit, context: TaskAttemptContext) = split match {
        case fileSplit: FileSplit =>
          val job = context.getConfiguration
          val fileSystem = FileSystem.get(job)
          val localFileSystem = FileSystem.getLocal(job)

          // Copy file from HDFS to local file system (verified temporary for successful and unsuccessful jobs).
          // This feels wrong, but I don't see a way around it.
          val name = fileSplit.getPath.getName
          fileSystem.copyToLocalFile(false, fileSplit.getPath, localFileSystem.getWorkingDirectory)

          val file = localFileSystem.pathToFile(new Path(localFileSystem.getWorkingDirectory, name))
          reader = new FreeHepRootTTreeReader[CASE](file.getAbsolutePath, ttreeLocation, implicitly[RootTTreeRowBuilder[CASE]])
      }

      override def nextKeyValue(): Boolean = {
        row += 1L
        if (row < reader.size) {
          key = KeyWritable(row)
          value = valueConstructor.newInstance().asInstanceOf[WRITABLE]
          value.put(reader.get(row))
          true
        }
        else
          false
      }
      override def getCurrentKey() = key
      override def getCurrentValue() = value
      override def getProgress() = row.toFloat / reader.size.toFloat
      override def close() {
        reader.release()
      }
    }
  }
}
