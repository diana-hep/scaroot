package org.dianahep.scaroot;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Library;
import org.bridj.ann.Constructor;
import org.bridj.cpp.CPPObject;

@Library(value = "Core", dependencies = {"z", "stdc++", "m", "gcc_s"})
public class TIter extends CPPObject {
    static { BridJ.register(); }

    @Constructor(0)
    public TIter(TIter titer) {
        super((Void)null, 0, titer);
    }

    // @Constructor(1)
    // public TIter(Pointer<TIterator> titerator) {
    //     super((Void)null, 1, titerator);
    // }

    public native Pointer<TObject> Next();

    public native TIter Begin();
}
