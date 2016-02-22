#ifndef _ROOTTREEREADER_H
#define _ROOTTREEREADER_H

#include <TFile.h>
#include <TTreeReader.h>
#include <TTreeReaderValue.h>
#include <TTreeReaderArray.h>

class RootTreeReader {
protected:
  TFile *tfile;
  TTreeReader *reader;
  bool valid = false;
  const char *errstring = nullptr;

public:
  RootTreeReader(const char *fileLocation, const char *treeLocation, int size, const char **names, const char **types);

};

#endif // ROOTTREEREADER_H
