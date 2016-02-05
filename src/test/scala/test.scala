package test.scala.scaroot

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import com.sun.jna._

class DefaultSuite extends FlatSpec with Matchers {
  "libm" should "do calculate sin(0.5), without and with native optimization" in {
    trait LibM extends Library {
      def sin(d: Double): Double
    }
    val libm = Native.loadLibrary("m", classOf[LibM]).asInstanceOf[LibM]

    object LibM2 {
      Native.register("m")

      @native
      def sin(d: Double): Double
    }

    println(s"sin(0.5) is ${libm.sin(0.5)} vs ${LibM2.sin(0.5)} vs ${Math.sin(0.5)}")
  }

  "custom shared object" should "do something, anything" in {
    trait LibScaROOT extends Library {
      def _Z10getEntriesPcS_(fileName: String, treeName: String): Int
    }
    val libScaROOT = Native.loadLibrary("scaroot.so", classOf[LibScaROOT]).asInstanceOf[LibScaROOT]

    println(s"""hey ${libScaROOT._Z10getEntriesPcS_("one", "two")}""")

  }

  it should "not crash" in {
    object LibJaROOT extends Library {
      Native.register("target/classes/jaroot.so")
      @native def Java_org_dianahep_jaroot_TFileWrapper_new_1TFile(fileName: String): Long
      @native def Java_org_dianahep_jaroot_TFileWrapper_delete_1TFile(pointer: Long): Unit
      @native def Java_org_dianahep_jaroot_TFileWrapper_TFile_1ls(pointer: Long): Unit
    }

    val pointer = LibJaROOT.Java_org_dianahep_jaroot_TFileWrapper_new_1TFile("/opt/root/test/Event.root")
    //  *** Break *** segmentation violation
    //  Generating stack trace...
    // /usr/bin/addr2line: '/tmp/jna--203800637/jna1322466507952658425.tmp': No such file
    // /usr/bin/addr2line: '/tmp/jna--203800637/jna1322466507952658425.tmp': No such file
    // /usr/bin/addr2line: '/tmp/jna--203800637/jna1322466507952658425.tmp': No such file
    // /usr/bin/addr2line: '/tmp/jna--203800637/jna1322466507952658425.tmp': No such file
    //  0x00007ff464d3265c in ffi_call_unix64 + 0x4c from /tmp/jna--203800637/jna1322466507952658425.tmp
    //  0x00007ff464d32164 in ffi_call + 0x1d4 from /tmp/jna--203800637/jna1322466507952658425.tmp
    //  0x00007ff464d27489 in <unknown> from /tmp/jna--203800637/jna1322466507952658425.tmp
    //  0x00007ff464d32398 in ffi_closure_unix64_inner + 0x88 from /tmp/jna--203800637/jna1322466507952658425.tmp
    //  0x00007ff464d327c4 in ffi_closure_unix64 + 0x46 from /tmp/jna--203800637/jna1322466507952658425.tmp
    //  0x00007ff46d0153f4 in <unknown function>



  }
}
