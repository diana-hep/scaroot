package org.dianahep.scaroot;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Library;
import org.bridj.cpp.CPPObject;

@Library(value = "Cling", dependencies = {"RIO", "tinfo", "z", "Core", "stdc++", "gcc_s", "Thread"})
public class TCling extends CPPObject {
    static { BridJ.register(); }
    public native boolean Declare(Pointer<Byte> code);

    //    void    ExecuteWithArgsAndReturn(TMethod* method, void* address, const void* args[] = 0, int nargs = 0, void* ret= 0) const;
    //   DeclId_t GetFunction(ClassInfo_t *cl, const char *funcname);

}
