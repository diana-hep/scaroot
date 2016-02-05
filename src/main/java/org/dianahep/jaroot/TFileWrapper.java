package org.dianahep.jaroot;

import java.io.File;

public class TFileWrapper {
    static {
        System.load((new File("target/classes/jaroot.so")).getAbsolutePath());
    }
    public native long new_TFile(String fileName);
    public native void delete_TFile(long pointer);
    public native void TFile_ls(long pointer);
}

// make .h file with:
//     javah -cp target/scaroot-TRUNK.jar -o src/main/cpp/org_dianahep_jaroot_TFileWrapper.h org.dianahep.jaroot.TFileWrapper

// compile .cpp with:
//     g++ -fPIC -shared -I/usr/lib/jvm/java-8-oracle/include -I/usr/lib/jvm/java-8-oracle/include/linux -Wl,--no-as-needed `root-config --cflags --ldflags --libs` -o target/classes/jaroot.so src/main/cpp/org_dianahep_jaroot_TFileWrapper.cpp
