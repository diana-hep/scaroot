package org.dianahep.scaroot;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Library;
import org.bridj.cpp.CPPObject;

@Library(value = "Core", dependencies = {"z", "stdc++", "m", "gcc_s"})
public class TFunction extends CPPObject {
    static { BridJ.register(); }
    public native Pointer<?> GetDeclId();

    //   DeclId_t            GetDeclId() const;



}
