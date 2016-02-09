package org.dianahep

import com.sun.jna._

package scaroot {
  object SharedObject extends Library {
    Native.register("/resources/native/scaroot.so")
    @native def new_TFile(fileName: String): Long
    @native def delete_TFile(pointer: Long): Unit
    @native def TFile_ls(pointer: Long): Unit
  }

  object Main {
    def main(args: Array[String]) {
      val pointer = SharedObject.new_TFile("/opt/root/test/Event.root")
      println(s"pointer value $pointer")
      SharedObject.TFile_ls(pointer)
      println(s"see a listing?")
      SharedObject.delete_TFile(pointer)
      println(s"still here?")
    }
  }
}
