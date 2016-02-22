#ifndef _ROOTTREEREADER_H
#define _ROOTTREEREADER_H

#include <TFile.h>
#include <TTreeReader.h>
#include <TTreeReaderValue.h>
#include <TTreeReaderArray.h>

TTreeReaderValue<float>* newValue_float(TTreeReader &reader, const char *name);

class RootTreeReader {
private:
  TFile *tfile;
  TTreeReader *reader;
  ROOT::Internal::TTreeReaderValueBase **values;

  bool valid = false;
  const char *errstring = nullptr;

public:
  RootTreeReader(const char *fileLocation, const char *treeLocation, int size, const char **names, const char **types);
  bool next();
  void *get(int index);
};

#endif // ROOTTREEREADER_H
