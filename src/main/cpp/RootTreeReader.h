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

public:
  RootTreeReader(const char *fileLocation, const char *treeLocation);
};

#endif // ROOTTREEREADER_H
