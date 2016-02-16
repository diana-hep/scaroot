package org.dianahep.scaroot

import scala.collection.JavaConversions._
import scala.language.experimental.macros 
import scala.reflect.macros.blackbox.Context

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
import org.apache.hadoop.mapreduce.RecordReader
import org.apache.hadoop.mapreduce.TaskAttemptContext

import org.dianahep.scaroot.freehep.FreeHepRootTTreeReader
import org.dianahep.scaroot.native.NativeRootTTreeReader
import org.dianahep.scaroot.api.RootTTreeRowBuilder

package hadoop {  
  trait HadoopWritable[CASE] extends Writable {
    private var wrapped = null.asInstanceOf[CASE]
    def isEmpty: Boolean
    def get: CASE
    def put(x: CASE) {
      wrapped = x
    }
  }

  object HadoopWritable {
    def unapply[CASE](x: HadoopWritable[CASE]): Option[CASE] =
      if (x.isEmpty)
        None
      else
        Some(x.wrapped)

    implicit def empty[CASE]: HadoopWritable[CASE] = macro emptyImpl[CASE]

    def emptyImpl[CASE : c.WeakTypeTag](c: Context): c.Expr[HadoopWritable[CASE]] = {
      import c.universe._
      val caseType = weakTypeOf[CASE]

      val fields = caseType.decls.collectFirst {
        case m: MethodSymbol if (m.isPrimaryConstructor) => m
      }.get.paramLists.head

      val (readParams, writeStatements) = fields.map {field =>
        val fieldName = field.asTerm.name
        val NullaryMethodType(fieldType) = caseType.decl(fieldName).typeSignature

        if (fieldType =:= typeOf[Boolean])
          (q"in.readBoolean()", q"out.writeBoolean(wrapped.$fieldName)")
        else if (fieldType =:= typeOf[Byte])
          (q"in.readByte()", q"out.writeByte(wrapped.$fieldName)")
        else if (fieldType =:= typeOf[Char])
          (q"in.readChar()", q"out.writeChar(wrapped.$fieldName)")
        else if (fieldType =:= typeOf[Double])
          (q"in.readDouble()", q"out.writeDouble(wrapped.$fieldName)")
        else if (fieldType =:= typeOf[Float])
          (q"in.readFloat()", q"out.writeFloat(wrapped.$fieldName)")
        else if (fieldType =:= typeOf[Int])
          (q"in.readInt()", q"out.writeInt(wrapped.$fieldName)")
        else if (fieldType =:= typeOf[Long])
          (q"in.readLong()", q"out.writeLong(wrapped.$fieldName)")
        else if (fieldType =:= typeOf[Short])
          (q"in.readShort()", q"out.writeShort(wrapped.$fieldName)")
        else if (fieldType =:= typeOf[String])
          (q"in.readUTF()", q"out.writeUTF(wrapped.$fieldName)")
        else
          throw new NotImplementedError(s"no handler for type $fieldType")
      }.unzip

      c.Expr[HadoopWritable[CASE]](q"""
        import org.dianahep.scaroot.hadoop._
        new HadoopWritable[$caseType] {
          def isEmpty = (wrapped == null)
          def get =
            if (isEmpty) throw new java.util.NoSuchElementException("HadoopWritable does not contain any data.")
            else wrapped
          def readFields(in: java.io.DataInput) { new $caseType(..$readParams) }
          def write(out: java.io.DataOutput) { ..$writeStatements }
        }
      """)
    }
  }

  abstract class RootInputFormat[CASE : RootTTreeRowBuilder : HadoopWritable](ttreeLocation: String) extends FileInputFormat[LongWritable, HadoopWritable[CASE]] {
    override def createRecordReader(split: InputSplit, context: TaskAttemptContext): RecordReader[LongWritable, HadoopWritable[CASE]] =
      new RootRecordReader

    override def isSplitable(context: JobContext, file: Path): Boolean = false

    override def getSplits(job: JobContext): java.util.List[InputSplit] =
      super.getSplits(job)  // does the right thing; this is here as a reminder that it's overridable

    class RootRecordReader extends RecordReader[LongWritable, HadoopWritable[CASE]] {
      private var reader: FreeHepRootTTreeReader[CASE] = null
      private var row = -1L
      private var key: LongWritable = null
      private var value: HadoopWritable[CASE] = implicitly[HadoopWritable[CASE]]

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
          key = new LongWritable(row)
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

  // object RootInputFormat {
  //   def apply[CASE](ttreeLocation: String): RootInputFormat[CASE] = macro applyImpl[CASE]

  //   def applyImpl[CASE : c.WeakTypeTag](c: Context)(ttreeLocation: c.Expr[String]): c.Expr[RootInputFormat[CASE]] = {
  //     import c.universe._
  //     val caseType = weakTypeOf[CASE]

  //     c.Expr[RootInputFormat[CASE]](q"""
  //       import org.dianahep.scaroot.hadoop._
  //       new RootInputFormat[$caseType] {
  //         val ttreeLocation = $ttreeLocation
  //       }
  //     """)
  //   }
  // }


}
