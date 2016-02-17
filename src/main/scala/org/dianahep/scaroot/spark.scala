package org.dianahep.scaroot

import scala.language.experimental.macros
// import scala.reflect._    (ClassTag)
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

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

// import org.dianahep.scaroot.api._
import org.dianahep.scaroot.hadoop._

package object spark {
  // def specificInputFormat[CASE](ttreeLocation: String) = macro specificInputFormatImpl[CASE]

  // def specificInputFormatImpl[CASE : c.WeakTypeTag](c: Context)(ttreeLocation: c.Expr[String]) = {
  //   import c.universe._
  //   val caseType = weakTypeOf[CASE]

  //   println(s"caseType $caseType")
  //   println(s"ttreeLocation $ttreeLocation")

  //   c.Expr[AnyRef](q"""
  //     import org.dianahep.scaroot.hadoop._
  //     class SpecificWritable extends ValueWritable[$caseType]
  //     class SpecificInputFormat extends RootInputFormat[$caseType, SpecificWritable]($ttreeLocation)
  //     new SpecificInputFormat
  //   """)
  // }

  implicit class SparkContextWrapper(sc: SparkContext) {
    // def rootRDD[T : RootTTreeRowBuilder : ValueSerializer : ClassTag](path: String, ttreeLocation: String): RDD[T] = {
    //   class SpecificWritable extends ValueWritable[T]
    //   class SpecificInputFormat extends RootInputFormat[T, SpecificWritable](ttreeLocation)
    //   sc.newAPIHadoopFile[KeyWritable, SpecificWritable, SpecificInputFormat](path) map {case (_, ValueWritable(x)) => x}
    // }

    // if (sc.getConf.get("spark.serializer", "none") != "org.apache.spark.serializer.KryoSerializer")
    //   complain about it!
    // sc.getConf.registerKryoClasses(Array(classTag[T].runtimeClass, classOf[KeyWritable], classOf[ValueWritable], classOf[ValueSerializer], classOf[SpecificWritable], classOf[SpecificInputFormat]))

    // def quicktest[T : RootTTreeRowBuilder : ValueSerializer : ClassTag]: RDD[T] = {
    //   rootRDD[T]("/home/ubuntu/TrackResonanceNtuple.root", "TrackResonanceNtuple/twoMuon")
    //   // sc.newAPIHadoopFile[KeyWritable, SpecificWritable2, SpecificInputFormat2]("/home/ubuntu/TrackResonanceNtuple.root")
    // }
  }
}
