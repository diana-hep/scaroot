package org.dianahep.scaroot

import scala.reflect.ClassTag

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import org.dianahep.scaroot.api._
import org.dianahep.scaroot.hadoop._

package object spark {
  implicit class SparkContextWrapper(sc: SparkContext) {
    def rootRDD[T : RootTTreeRowBuilder : ValueSerializer : ClassTag](path: String, ttreeLocation: String): RDD[T] = {
      class SpecificWritable extends ValueWritable[T]
      class SpecificInputFormat extends RootInputFormat[T, SpecificWritable](ttreeLocation)

      // if (sc.getConf.get("spark.serializer", "none") != "org.apache.spark.serializer.KryoSerializer")
      //   complain about it!

      // sc.getConf.registerKryoClasses(Array(classTag[T].runtimeClass, classOf[KeyWritable], classOf[ValueWritable], classOf[ValueSerializer], classOf[SpecificWritable], classOf[SpecificInputFormat]))

      sc.newAPIHadoopFile[KeyWritable, SpecificWritable, SpecificInputFormat](path) map {case (_, ValueWritable(x)) => x}
    }

    def quicktest[T : RootTTreeRowBuilder : ValueSerializer : ClassTag]: RDD[T] = {
      rootRDD[T]("/home/ubuntu/TrackResonanceNtuple.root", "TrackResonanceNtuple/twoMuon")
      // sc.newAPIHadoopFile[KeyWritable, SpecificWritable2, SpecificInputFormat2]("/home/ubuntu/TrackResonanceNtuple.root")
    }
  }
}
