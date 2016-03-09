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

  void execute0(TMethod *tmethod, void *instance, void *ret);
  void execute1(TMethod *tmethod, void *instance, void *arg0, void *ret);
  void execute2(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *ret);
  void execute3(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *ret);
  void execute4(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *ret);
  void execute5(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *ret);
  void execute6(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *ret);
  void execute7(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *ret);
  void execute8(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *ret);
  void execute9(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *ret);
  void execute10(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *ret);
  void execute11(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *ret);
  void execute12(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *ret);
  void execute13(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *ret);
  void execute14(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *ret);
  void execute15(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *arg14, void *ret);
  void execute16(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *arg14, void *arg15, void *ret);
  void execute17(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *arg14, void *arg15, void *arg16, void *ret);
  void execute18(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *arg14, void *arg15, void *arg16, void *arg17, void *ret);
  void execute19(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *arg14, void *arg15, void *arg16, void *arg17, void *arg18, void *ret);
  void execute20(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *arg14, void *arg15, void *arg16, void *arg17, void *arg18, void *arg19, void *ret);
  void execute21(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *arg14, void *arg15, void *arg16, void *arg17, void *arg18, void *arg19, void *arg20, void *ret);
  void execute22(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *arg14, void *arg15, void *arg16, void *arg17, void *arg18, void *arg19, void *arg20, void *arg21, void *ret);
}

#endif // ROOT_ACCESS_H
