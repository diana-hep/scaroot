// g++ -fPIC -shared -Wl,--no-as-needed `root-config --cflags --ldflags --libs` -o target/classes/scaroot.so src/main/cpp/scaroot.cpp

#include <stdint.h>

#include "TFile.h"

extern "C" {
  int64_t new_TFile(char *fileName);
  void delete_TFile(int64_t pointer);
  void TFile_ls(int64_t pointer);
}

int64_t new_TFile(char *fileName) {
  TFile *tfile = new TFile(fileName);
  return (int64_t)tfile;
}

void delete_TFile(int64_t pointer) {
  TFile *tfile = (TFile*)pointer;
  delete tfile;
}

void TFile_ls(int64_t pointer) {
  TFile *tfile = (TFile*)pointer;
  tfile->ls();
}
