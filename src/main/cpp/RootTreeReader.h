#ifndef ROOTTREEREADER_H
#define ROOTTREEREADER_H

#include <TFile.h>
#include <TTreeReader.h>
#include <TTreeReaderValue.h>
#include <TTreeReaderArray.h>

using namespace ROOT::Internal;

extern "C" {
  TFile *newFile(const char *fileLocation);
  TTreeReader *newReader(TFile *file, const char *treeLocation);
  bool readerNext(TTreeReader *reader);

  TTreeReaderValueBase *newValue_float(TTreeReader *reader, const char *name);

  float getValue_float(TTreeReaderValueBase *value);
}

#endif // ROOTTREEREADER_H
