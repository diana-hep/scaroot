package org.dianahep.scaroot;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Library;
import org.bridj.cpp.CPPObject;

@Library(value = "Core", dependencies = {"z", "stdc++", "m", "gcc_s"})
public class TClass extends CPPObject {
    static { BridJ.register(); }
    public native static Pointer<TClass> GetClass(Pointer<Byte> name, boolean load, boolean silent);
    public native Pointer<TList> GetListOfAllPublicMethods(boolean load);
}
