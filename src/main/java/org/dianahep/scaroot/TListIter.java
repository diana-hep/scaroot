package org.dianahep.scaroot;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Library;
import org.bridj.ann.Constructor;
import org.bridj.cpp.CPPObject;

@Library(value = "Core", dependencies = {"z", "stdc++", "m", "gcc_s"})
public class TListIter extends CPPObject {
    static { BridJ.register(); }

    @Constructor(0)
    public TListIter(Pointer<TList> tlist, boolean dir) {
        super((Void)null, 0, tlist, dir);
    }

    public native Pointer<TObject> Next();
}
