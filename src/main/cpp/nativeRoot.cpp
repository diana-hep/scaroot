#include <stdint.h>
#include <iostream>

#include "TFile.h"
#include "TTree.h"
#include "TLeaf.h"

#include "TLeafB.h"
#include "TLeafS.h"
#include "TLeafI.h"
#include "TLeafL.h"
#include "TLeafF.h"
#include "TLeafD.h"
#include "TLeafC.h"

extern "C" {
  int64_t new_TFile(const char *rootFileLocation);
  void close_TFile(int64_t tfile);
  void delete_TFile(int64_t tfile);
  int64_t getTTree(int64_t tfile, const char *ttreeLocation);

  int64_t ttreeGetNumEntries(int64_t ttree);
  int64_t ttreeGetNumLeaves(int64_t ttree);
  int64_t ttreeGetLeaf(int64_t ttree, int64_t i);

  const char *ttreeGetLeafName(int64_t tleaf);
  const char *ttreeGetLeafType(int64_t tleaf);

  int64_t new_dummy(int64_t ttree, int64_t tleaf);
  void delete_dummy(int64_t dummy);
  int8_t ttreeGetRow(int64_t ttree, int64_t row);

  int8_t getValueLeafB(int64_t tleaf);
  int16_t getValueLeafS(int64_t tleaf);
  int32_t getValueLeafI(int64_t tleaf);
  int64_t getValueLeafL(int64_t tleaf);
  float getValueLeafF(int64_t tleaf);
  double_t getValueLeafD(int64_t tleaf);
  const char *getValueLeafC(int64_t tleaf);
}

int64_t new_TFile(const char *rootFileLocation) {
  TFile *tfile = new TFile(rootFileLocation);
  return (int64_t)tfile;
}

void close_TFile(int64_t tfile) {
  TFile *tfile_ptr = (TFile*)tfile;
  tfile_ptr->Close();
}

void delete_TFile(int64_t tfile) {
  TFile *tfile_ptr = (TFile*)tfile;
  delete tfile_ptr;
}

int64_t getTTree(int64_t tfile, const char *ttreeLocation) {
  TTree *ttree;
  TFile *tfile_ptr = (TFile*)tfile;
  tfile_ptr->GetObject(ttreeLocation, ttree);
  return (int64_t)ttree;
}

int64_t ttreeGetNumEntries(int64_t ttree) {
  TTree *ttree_ptr = (TTree*)ttree;
  return ttree_ptr->GetEntries();
}

int64_t ttreeGetNumLeaves(int64_t ttree) {
  TTree *ttree_ptr = (TTree*)ttree;
  TObjArray *tObjArray = ttree_ptr->GetListOfLeaves();
  return tObjArray->GetEntries();
}

int64_t ttreeGetLeaf(int64_t ttree, int64_t i) {
  TTree *ttree_ptr = (TTree*)ttree;
  TObjArray *tObjArray = ttree_ptr->GetListOfLeaves();
  return (int64_t)(tObjArray->At(i));
}

const char *ttreeGetLeafName(int64_t tleaf) {
  TLeaf *tleaf_ptr = (TLeaf*)tleaf;
  return tleaf_ptr->GetName();
}

const char *ttreeGetLeafType(int64_t tleaf) {
  TLeaf *tleaf_ptr = (TLeaf*)tleaf;
  return tleaf_ptr->GetTypeName();
}

// UNFINISHED BELOW

int64_t new_dummy(int64_t ttree, int64_t tleaf) {
  TTree *ttree_ptr = (TTree*)ttree;
  TLeaf *tleaf_ptr = (TLeaf*)tleaf;
  std::string typeName = std::string(ttreeGetLeafType(tleaf));
  if (typeName == std::string("TLeafB")) {
    int8_t *dummy = new int8_t;
    tleaf_ptr->GetBranch()->SetAddress(dummy);
    return (int64_t)dummy;
  }
  else if (typeName == std::string("TLeafS")) {
    int16_t *dummy = new int16_t;
    tleaf_ptr->GetBranch()->SetAddress(dummy);
    return (int64_t)dummy;
  }
  else if (typeName == std::string("TLeafI")) {
    int32_t *dummy = new int32_t;
    tleaf_ptr->GetBranch()->SetAddress(dummy);
    return (int64_t)dummy;
  }
  else if (typeName == std::string("TLeafL")) {
    int64_t *dummy = new int64_t;
    tleaf_ptr->GetBranch()->SetAddress(dummy);
    return (int64_t)dummy;
  }
  else if (typeName == std::string("TLeafF")) {
    float *dummy = new float;
    tleaf_ptr->GetBranch()->SetAddress(dummy);
    return (int64_t)dummy;
  }
  else if (typeName == std::string("TLeafD")) {
    double *dummy = new double;
    tleaf_ptr->GetBranch()->SetAddress(dummy);
    return (int64_t)dummy;
  }
  else {
    std::cout << "HELP " << typeName << std::endl;
  }
}

void delete_dummy(int64_t dummy) {
  void *dummy_ptr = (void*)dummy;
  delete dummy_ptr;
}

int8_t ttreeGetRow(int64_t ttree, int64_t row) {
  TTree *ttree_ptr = (TTree*)ttree;  
  ttree_ptr->GetEntry(row);
  // if failure, return 0;
  return 1;
}

int8_t getValueLeafB(int64_t dummy) {
  return *((int8_t*)dummy);
}

int16_t getValueLeafS(int64_t dummy) {
  return *((int16_t*)dummy);
}

int32_t getValueLeafI(int64_t dummy) {
  return *((int32_t*)dummy);
}

int64_t getValueLeafL(int64_t dummy) {
  return *((int64_t*)dummy);
}

float getValueLeafF(int64_t dummy) {
  return *((float*)dummy);
}

double getValueLeafD(int64_t dummy) {
  return *((double*)dummy);
}

const char *getValueLeafC(int64_t tleaf) {
  TLeafC *tleaf_ptr = (TLeafC*)tleaf;
  return tleaf_ptr->GetValueString();
}
