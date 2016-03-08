package org.dianahep.scaroot;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Library;
import org.bridj.cpp.CPPObject;

@Library(value = "Core", dependencies = {"z", "stdc++", "m", "gcc_s"})
public class TInterpreter extends CPPObject {
    static {
        System.loadLibrary("Core");
        System.loadLibrary("RIO");
        System.loadLibrary("Net");
        System.loadLibrary("Hist");
        System.loadLibrary("Graf");
        System.loadLibrary("Graf3d");
        System.loadLibrary("Gpad");
        System.loadLibrary("Tree");
        System.loadLibrary("Rint");
        System.loadLibrary("Postscript");
        System.loadLibrary("Matrix");
        System.loadLibrary("Physics");
        System.loadLibrary("MathCore");
        System.loadLibrary("Thread");
        System.loadLibrary("MultiProc");
        System.loadLibrary("m");
        System.loadLibrary("dl");
        System.loadLibrary("Cling");

        BridJ.register();
    }
    
    public native static Pointer<TInterpreter> Instance();
}
