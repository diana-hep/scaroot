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
  abstract class RootInputFormat[CASE: RootTTreeRowBuilder](ttreeLocation: String) extends FileInputFormat[LongWritable, CASE] {
    override def createRecordReader(split: InputSplit, context: TaskAttemptContext): RecordReader[LongWritable, CASE] =
      new RootRecordReader

    override def isSplitable(context: JobContext, file: Path): Boolean = false

    override def getSplits(job: JobContext): java.util.List[InputSplit] =
      super.getSplits(job)  // does the right thing; this is here as a reminder that it's overridable

    class RootRecordReader extends RecordReader[LongWritable, CASE] {
      private var reader: FreeHepRootTTreeReader[CASE] = null
      private var row = -1L
      private var key: LongWritable = null
      private var value: CASE = null.asInstanceOf[CASE]

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
          value = reader.get(row)
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