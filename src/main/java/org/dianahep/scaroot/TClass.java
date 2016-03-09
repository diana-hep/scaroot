package org.dianahep.scaroot;

import java.util.Iterator;
import java.util.Collections;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.FlagSet;
import org.bridj.IntValuedEnum;
import org.bridj.ann.Library;
import org.bridj.ann.Ptr;
import org.bridj.cpp.CPPObject;

@Library(value = "Core", dependencies = {"z", "stdc++", "m", "gcc_s"})
public class TClass extends CPPObject {
    static { BridJ.register(); }

    public enum ENewType implements IntValuedEnum<ENewType > {
        kRealNew(0),
            kClassNew(1),
            kDummyNew(2);
        ENewType(long value) {
            this.value = value;
        }
        public final long value;
        public long value() {
            return this.value;
        }
        public Iterator<ENewType > iterator() {
            return Collections.singleton(this).iterator();
        }
        public static IntValuedEnum<ENewType > fromValue(int value) {
            return FlagSet.fromValue(value, values());
        }
    };
 
    public native static Pointer<TClass> GetClass(Pointer<Byte> name, boolean load, boolean silent);
    public native Pointer<TList> GetListOfMethods(boolean load);

    public Pointer<? > New(IntValuedEnum<TClass.ENewType > defConstructor, boolean quiet) {
        return Pointer.pointerToAddress(New((int)defConstructor.value(), quiet));
    }
    @Ptr 
    protected native long New(int defConstructor, boolean quiet);

}
