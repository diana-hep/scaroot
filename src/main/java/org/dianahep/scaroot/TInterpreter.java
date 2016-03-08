package org.dianahep.scaroot;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Library;
import org.bridj.cpp.CPPObject;

@Library(value = "Core", dependencies = {"z", "stdc++", "m", "gcc_s"})
public class TInterpreter extends CPPObject {
    static { BridJ.register(); }
    public native static Pointer<TInterpreter> Instance();
}
