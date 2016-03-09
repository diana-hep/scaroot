#ifndef ROOT_ACCESS_H
#define ROOT_ACCESS_H

#include <TSystem.h>
#include <TInterpreter.h>
#include <TClass.h>
#include <TMethod.h>
#include <TMethodArg.h>

extern "C" {
  void resetSignals();

  bool declare(const char *code);

  TClass *tclass(const char *name);
  void *newInstance(TClass *tclass);

  int numMethods(TClass *tclass);
  TMethod *tmethod(TClass *tclass, int methodIndex);
  const char *tmethodName(TMethod *tmethod);
  int tmethodNumArgs(TMethod *tmethod);
  const char *tmethodArg(TMethod *tmethod, int argIndex);

  int execute(TMethod *tmethod, void *instance, int arg0, int arg1);
}

#endif // ROOT_ACCESS_H
