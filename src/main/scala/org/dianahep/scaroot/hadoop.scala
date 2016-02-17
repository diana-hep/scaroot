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

// package hadoop {  
//   case class KeyWritable(ttreeEntry: Long) extends LongWritable(ttreeEntry)

//   trait ValueSerializer[CASE] {
//     def read(in: java.io.DataInput): CASE
//     def write(out: java.io.DataOutput, obj: CASE)
//   }
//   object ValueSerializer {
//     implicit def compileValueSerializer[CASE]: ValueSerializer[CASE] = macro compileValueSerializerImpl[CASE]

//     def compileValueSerializerImpl[CASE : c.WeakTypeTag](c: Context): c.Expr[ValueSerializer[CASE]] = {
//       import c.universe._
//       val caseType = weakTypeOf[CASE]

//       // val fields = caseType.decls.collectFirst {
//       //   case m: MethodSymbol if (m.isPrimaryConstructor) => m
//       // }.get.paramLists.head
//       val fields = caseType.declarations.collectFirst {
//         case m: MethodSymbol if (m.isPrimaryConstructor) => m
//       }.get.paramss.head

//       val (readParams, writeStatements) = fields.map {field =>
//         val fieldName = field.asTerm.name
//         // val NullaryMethodType(fieldType) = caseType.decl(fieldName).typeSignature
//         val NullaryMethodType(fieldType) = caseType.declaration(fieldName).typeSignature

//         if (fieldType =:= typeOf[Boolean])
//           (q"in.readBoolean()", q"out.writeBoolean(obj.$fieldName)")
//         else if (fieldType =:= typeOf[Byte])
//           (q"in.readByte()", q"out.writeByte(obj.$fieldName)")
//         else if (fieldType =:= typeOf[Char])
//           (q"in.readChar()", q"out.writeChar(obj.$fieldName)")
//         else if (fieldType =:= typeOf[Double])
//           (q"in.readDouble()", q"out.writeDouble(obj.$fieldName)")
//         else if (fieldType =:= typeOf[Float])
//           (q"in.readFloat()", q"out.writeFloat(obj.$fieldName)")
//         else if (fieldType =:= typeOf[Int])
//           (q"in.readInt()", q"out.writeInt(obj.$fieldName)")
//         else if (fieldType =:= typeOf[Long])
//           (q"in.readLong()", q"out.writeLong(obj.$fieldName)")
//         else if (fieldType =:= typeOf[Short])
//           (q"in.readShort()", q"out.writeShort(obj.$fieldName)")
//         else if (fieldType =:= typeOf[String])
//           (q"in.readUTF()", q"out.writeUTF(obj.$fieldName)")
//         else
//           throw new NotImplementedError(s"no handler for type $fieldType")
//       }.unzip

//       c.Expr[ValueSerializer[CASE]](q"""
//         import org.dianahep.scaroot.hadoop._
//         new ValueSerializer[$caseType] {
//           def read(in: java.io.DataInput) = new $caseType(..$readParams)
//           def write(out: java.io.DataOutput, obj: $caseType) { ..$writeStatements }
//         }
//       """)
//     }
//   }

//   abstract class ValueWritable[CASE : ValueSerializer] extends Writable {
//     private var wrapped = null.asInstanceOf[CASE]
//     def isEmpty = (wrapped == null)
//     def get =
//       if (isEmpty) throw new java.util.NoSuchElementException("ValueWritable does not contain any data (instantiated but not deserialized).")
//       else wrapped
//     def put(x: CASE) {
//       wrapped = x
//     }
//     def readFields(in: java.io.DataInput) {
//       wrapped = implicitly[ValueSerializer[CASE]].read(in)
//     }
//     def write(out: java.io.DataOutput) {
//       implicitly[ValueSerializer[CASE]].write(out, get)
//     }
//     override def toString() =
//       if (isEmpty)
//         "ValueWritable()"
//       else
//         "ValueWritable(" + get.toString + ")"
//   }
//   object ValueWritable {
//     def unapply[CASE](x: ValueWritable[CASE]): Option[CASE] =
//       if (x.isEmpty)
//         None
//       else
//         Some(x.get)
//   }

//   abstract class RootInputFormat[CASE : RootTTreeRowBuilder : ValueSerializer, WRITABLE <: ValueWritable[CASE] : ClassTag](ttreeLocation: String) extends FileInputFormat[KeyWritable, WRITABLE] {
//     override def createRecordReader(split: InputSplit, context: TaskAttemptContext): RecordReader[KeyWritable, WRITABLE] =
//       new RootRecordReader

//     override def isSplitable(context: JobContext, file: Path): Boolean = false

//     override def getSplits(job: JobContext): java.util.List[InputSplit] =
//       super.getSplits(job)  // does the right thing; this is here as a reminder that it's overridable

//     class RootRecordReader extends RecordReader[KeyWritable, WRITABLE] {
//       private var reader: FreeHepRootTTreeReader[CASE] = null
//       private var row = -1L
//       private var key = null.asInstanceOf[KeyWritable]
//       private var value = null.asInstanceOf[WRITABLE]
//       private val valueConstructor = classTag[WRITABLE].runtimeClass.getConstructor()
//       valueConstructor.setAccessible(true)

//       override def initialize(split: InputSplit, context: TaskAttemptContext) = split match {
//         case fileSplit: FileSplit =>
//           val job = context.getConfiguration
//           val fileSystem = FileSystem.get(job)
//           val localFileSystem = FileSystem.getLocal(job)

//           // Copy file from HDFS to local file system (verified temporary for successful and unsuccessful jobs).
//           // This feels wrong, but I don't see a way around it.
//           val name = fileSplit.getPath.getName
//           fileSystem.copyToLocalFile(false, fileSplit.getPath, localFileSystem.getWorkingDirectory)

//           val file = localFileSystem.pathToFile(new Path(localFileSystem.getWorkingDirectory, name))
//           reader = new FreeHepRootTTreeReader[CASE](file.getAbsolutePath, ttreeLocation, implicitly[RootTTreeRowBuilder[CASE]])
//       }

//       override def nextKeyValue(): Boolean = {
//         row += 1L
//         if (row < reader.size) {
//           key = KeyWritable(row)
//           value = valueConstructor.newInstance().asInstanceOf[WRITABLE]
//           value.put(reader.get(row))
//           true
//         }
//         else
//           false
//       }
//       override def getCurrentKey() = key
//       override def getCurrentValue() = value
//       override def getProgress() = row.toFloat / reader.size.toFloat
//       override def close() {
//         reader.release()
//       }
//     }
//   }
// }
