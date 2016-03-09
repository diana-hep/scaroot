#include <iostream>

#include "RootAccess.h"

void resetSignals() {
  gSystem->ResetSignals();
}

bool declare(const char *code) {
  gInterpreter->Declare(code);
}

TClass *tclass(const char *name) {
  return TClass::GetClass(name);
}

void *newInstance(TClass *tclass) {
  return tclass->New(TClass::kClassNew, true);
}

int numMethods(TClass *tclass) {
  return tclass->GetListOfMethods()->GetSize();
}

TMethod *tmethod(TClass *tclass, int methodIndex) {
  return (TMethod*)tclass->GetListOfMethods()->At(methodIndex);
}

const char *tmethodName(TMethod *tmethod) {
  return tmethod->GetName();
}

int tmethodNumArgs(TMethod *tmethod) {
  return tmethod->GetListOfMethodArgs()->GetSize();
}

const char *tmethodArg(TMethod *tmethod, int argIndex) {
  return ((TMethodArg*)tmethod->GetListOfMethodArgs()->At(argIndex))->GetTypeNormalizedName().c_str();
}

int execute(TMethod *tmethod, void *instance, int arg0, int arg1) {
  const void *argv[2] = {&arg0, &arg1};
  int ret;
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 2, &ret);
  return ret;
}
