#include "TFile.h"

#include "org_dianahep_jaroot_TFileWrapper.h"

JNIEXPORT jlong JNICALL Java_org_dianahep_jaroot_TFileWrapper_new_1TFile(JNIEnv *env, jobject obj, jstring fileName) {
  const char *c_fileName = env->GetStringUTFChars(fileName, NULL);

  TFile *tfile = new TFile(c_fileName);

  env->ReleaseStringUTFChars(fileName, c_fileName);

  return (jlong)(tfile);
}

JNIEXPORT void JNICALL Java_org_dianahep_jaroot_TFileWrapper_delete_1TFile(JNIEnv *env, jobject obj, jlong pointer) {
  TFile *tfile = (TFile*)(pointer);
  delete tfile;
}

JNIEXPORT void JNICALL Java_org_dianahep_jaroot_TFileWrapper_TFile_1ls(JNIEnv *env, jobject obj, jlong pointer) {
  ((TFile*)(pointer))->ls();
}
