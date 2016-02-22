package org.dianahep.scaroot;

import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.annotation.*;

@Platform(include="../../../src/main/cpp/RootTreeReader.h")
public class RootTreeReader extends Pointer {
    static { Loader.load(); }
    public RootTreeReader() { allocate(); }
    private native void allocate();

    public native void set(String stuff);
    public native @StdString String get();
}
