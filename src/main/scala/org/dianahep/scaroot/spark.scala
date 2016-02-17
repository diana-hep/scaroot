package org.dianahep.scaroot

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import org.dianahep.scaroot.api._
import org.dianahep.scaroot.hadoop._

package object spark {
  implicit class SparkContextWrapper(sc: SparkContext) {
    def rootRDD[T : RootTTreeRowBuilder : ValueSerializer](path: String, ttreeLocation: String): RDD[T] = {
      class SpecificWritable extends ValueWritable[T]
      class SpecificInputFormat extends RootInputFormat[T, SpecificWritable](ttreeLocation)
      sc.newAPIHadoopFile[KeyWritable, SpecificWritable, SpecificInputFormat](path) map {case (_, ValueWritable(x)) => x}
    }

    def quicktest[T : RootTTreeRowBuilder : ValueSerializer]: RDD[T] = {
      rootRDD[T]("/home/ubuntu/TrackResonanceNtuple.root", "TrackResonanceNtuple/twoMuon")
      // sc.newAPIHadoopFile[KeyWritable, SpecificWritable2, SpecificInputFormat2]("/home/ubuntu/TrackResonanceNtuple.root")
    }
  }
}
