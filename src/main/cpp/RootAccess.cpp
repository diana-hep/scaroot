#include "RootAccess.h"

const char *rootVersion() {
  return ROOT_RELEASE;
}

int rootVersionCode() {
  return ROOT_VERSION_CODE;
}

int rootVersionCodeFrom(int major, int minor, int revision) {
  return ROOT_VERSION(major, minor, revision);
}

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

const char *tmethodArgType(TMethod *tmethod, int argIndex) {
  return ((TMethodArg*)tmethod->GetListOfMethodArgs()->At(argIndex))->GetTypeNormalizedName().c_str();
}

const char *tmethodRetType(TMethod *tmethod) {
  return tmethod->GetReturnTypeNormalizedName().c_str();
}

void execute0(TMethod *tmethod, void *instance, void *ret) {
  const void *argv[0] = {};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 0, ret);
}

void execute1(TMethod *tmethod, void *instance, void *arg0, void *ret) {
  const void *argv[1] = {arg0};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 1, ret);
}

void execute2(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *ret) {
  const void *argv[2] = {arg0, arg1};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 2, ret);
}

void execute3(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *ret) {
  const void *argv[3] = {arg0, arg1, arg2};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 3, ret);
}

void execute4(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *ret) {
  const void *argv[4] = {arg0, arg1, arg2, arg3};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 4, ret);
}

void execute5(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *ret) {
  const void *argv[5] = {arg0, arg1, arg2, arg3, arg4};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 5, ret);
}

void execute6(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *ret) {
  const void *argv[6] = {arg0, arg1, arg2, arg3, arg4, arg5};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 6, ret);
}

void execute7(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *ret) {
  const void *argv[7] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 7, ret);
}

void execute8(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *ret) {
  const void *argv[8] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 8, ret);
}

void execute9(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *ret) {
  const void *argv[9] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 9, ret);
}

void execute10(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *ret) {
  const void *argv[10] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 10, ret);
}

void execute11(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *ret) {
  const void *argv[11] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 11, ret);
}

void execute12(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *ret) {
  const void *argv[12] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 12, ret);
}

void execute13(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *ret) {
  const void *argv[13] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 13, ret);
}

void execute14(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *ret) {
  const void *argv[14] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 14, ret);
}

void execute15(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *arg14, void *ret) {
  const void *argv[15] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 15, ret);
}

void execute16(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *arg14, void *arg15, void *ret) {
  const void *argv[16] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 16, ret);
}

void execute17(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *arg14, void *arg15, void *arg16, void *ret) {
  const void *argv[17] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 17, ret);
}

void execute18(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *arg14, void *arg15, void *arg16, void *arg17, void *ret) {
  const void *argv[18] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 18, ret);
}

void execute19(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *arg14, void *arg15, void *arg16, void *arg17, void *arg18, void *ret) {
  const void *argv[19] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 19, ret);
}

void execute20(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *arg14, void *arg15, void *arg16, void *arg17, void *arg18, void *arg19, void *ret) {
  const void *argv[20] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 20, ret);
}

void execute21(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *arg14, void *arg15, void *arg16, void *arg17, void *arg18, void *arg19, void *arg20, void *ret) {
  const void *argv[21] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19, arg20};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 21, ret);
}

void execute22(TMethod *tmethod, void *instance, void *arg0, void *arg1, void *arg2, void *arg3, void *arg4, void *arg5, void *arg6, void *arg7, void *arg8, void *arg9, void *arg10, void *arg11, void *arg12, void *arg13, void *arg14, void *arg15, void *arg16, void *arg17, void *arg18, void *arg19, void *arg20, void *arg21, void *ret) {
  const void *argv[22] = {arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11, arg12, arg13, arg14, arg15, arg16, arg17, arg18, arg19, arg20, arg21};
  gInterpreter->ExecuteWithArgsAndReturn(tmethod, instance, argv, 22, ret);
}
