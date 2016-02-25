#include <string>

#include "RootTreeReader.h"

void resetSignals() {
  gSystem->ResetSignals();
}

TFile *newFile(const char *fileLocation) {
  return TFile::Open(fileLocation);
}

TTreeReader *newReader(TFile *file, const char *treeLocation) {
  return new TTreeReader(treeLocation, file);
}

bool readerNext(TTreeReader *reader) {
  return reader->Next();
}

TTreeReaderValueBase *newValue_float(TTreeReader *reader, const char *name) {
  return new TTreeReaderValue<float>(*reader, name);
}

float getValue_float(TTreeReaderValueBase *value) {
  return *((float*)value->GetAddress());
}
