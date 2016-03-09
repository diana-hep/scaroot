package org.dianahep.scaroot;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.ann.Library;
import org.bridj.cpp.CPPObject;

@Library(value = "Core", dependencies = {"z", "stdc++", "m", "gcc_s"})
public class TFunction extends CPPObject {
    static { BridJ.register(); }

    public native Pointer<Byte> GetName();
    public native Pointer<Byte> GetSignature();
    // public native long Property();


   // const char         *GetSignature();
   // const char         *GetReturnTypeName() const;
   // std::string         GetReturnTypeNormalizedName() const;
   // TList              *GetListOfMethodArgs();
   // Int_t               GetNargs() const;
   // Int_t               GetNargsOpt() const;
   // DeclId_t            GetDeclId() const;
   // void               *InterfaceMethod() const;
   // virtual Bool_t      IsValid();
   // virtual void        Print(Option_t *option="") const;
   // Long_t              Property() const;
   // Long_t              ExtraProperty() const;

}
