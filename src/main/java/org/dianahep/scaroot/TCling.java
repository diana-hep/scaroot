package org.dianahep.scaroot;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Library;
import org.bridj.cpp.CPPObject;

@Library(value = "Cling", dependencies = {"RIO", "tinfo", "z", "Core", "stdc++", "gcc_s", "Thread"})
public class TCling extends CPPObject {
    static { BridJ.register(); }
    public native boolean Declare(Pointer<Byte> code);
}
