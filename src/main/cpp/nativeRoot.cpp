#include <stdint.h>
#include <iostream>

#include "TFile.h"
#include "TKey.h"
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
  int8_t tfileIsOpen(int64_t tfile);
  int8_t tfileIsZombie(int64_t tfile);
  int64_t tfileNumKeys(int64_t tfile);
  const char *tfileKeyName(int64_t tfile, int64_t index);
  int64_t getTTree(int64_t tfile, const char *ttreeLocation);

  int64_t ttreeGetNumEntries(int64_t ttree);
  int64_t ttreeGetNumLeaves(int64_t ttree);
  int64_t ttreeGetLeaf(int64_t ttree, int64_t i);

  const char *ttreeGetLeafName(int64_t tleaf);
  const char *ttreeGetLeafType(int64_t tleaf);

  int64_t new_dummy(int64_t ttree, int64_t tleaf);
  void delete_dummyB(int64_t dummy);
  void delete_dummyS(int64_t dummy);
  void delete_dummyI(int64_t dummy);
  void delete_dummyL(int64_t dummy);
  void delete_dummyF(int64_t dummy);
  void delete_dummyD(int64_t dummy);
  int8_t ttreeGetRow(int64_t ttree, int64_t row);

  int8_t getValueLeafB(int64_t tleaf);
  int16_t getValueLeafS(int64_t tleaf);
  int32_t getValueLeafI(int64_t tleaf);
  int64_t getValueLeafL(int64_t tleaf);
  float_t getValueLeafF(int64_t tleaf);
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

int8_t tfileIsOpen(int64_t tfile) {
  TFile *tfile_ptr = (TFile*)tfile;
  return tfile_ptr->IsOpen();
}

int8_t tfileIsZombie(int64_t tfile) {
  TFile *tfile_ptr = (TFile*)tfile;
  return tfile_ptr->IsZombie();
}

int64_t tfileNumKeys(int64_t tfile) {
  TFile *tfile_ptr = (TFile*)tfile;
  return tfile_ptr->GetNkeys();
}

const char *tfileKeyName(int64_t tfile, int64_t index) {
  TFile *tfile_ptr = (TFile*)tfile;
  TList *keys = tfile_ptr->GetListOfKeys();
  return ((TNamed*)(keys->At(index)))->GetName();
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

int64_t new_dummy(int64_t ttree, int64_t tleaf) {
  TTree *ttree_ptr = (TTree*)ttree;
  TLeaf *tleaf_ptr = (TLeaf*)tleaf;
  std::string typeName = std::string(ttreeGetLeafType(tleaf));
  if (typeName == std::string("Int8_t")) {
    int8_t *dummy = new int8_t;
    tleaf_ptr->GetBranch()->SetAddress(dummy);
    return (int64_t)dummy;
  }
  else if (typeName == std::string("Int16_t")) {
    int16_t *dummy = new int16_t;
    tleaf_ptr->GetBranch()->SetAddress(dummy);
    return (int64_t)dummy;
  }
  else if (typeName == std::string("Int_t")) {
    int32_t *dummy = new int32_t;
    tleaf_ptr->GetBranch()->SetAddress(dummy);
    return (int64_t)dummy;
  }
  else if (typeName == std::string("Int64_t")) {
    int64_t *dummy = new int64_t;
    tleaf_ptr->GetBranch()->SetAddress(dummy);
    return (int64_t)dummy;
  }
  else if (typeName == std::string("Float_t")) {
    float_t *dummy = new float_t;
    tleaf_ptr->GetBranch()->SetAddress(dummy);
    return (int64_t)dummy;
  }
  else if (typeName == std::string("Double_t")) {
    double_t *dummy = new double_t;
    tleaf_ptr->GetBranch()->SetAddress(dummy);
    return (int64_t)dummy;
  }
  else if (typeName == std::string("Char_t")) {
    return (int64_t)tleaf;
  }
  else {
    std::cout << "HELP " << typeName << std::endl;
  }
}

void delete_dummyB(int64_t dummy) {
  int8_t *dummy_ptr = (int8_t*)dummy;
  delete dummy_ptr;
}

void delete_dummyS(int64_t dummy) {
  int16_t *dummy_ptr = (int16_t*)dummy;
  delete dummy_ptr;
}

void delete_dummyI(int64_t dummy) {
  int32_t *dummy_ptr = (int32_t*)dummy;
  delete dummy_ptr;
}

void delete_dummyL(int64_t dummy) {
  int64_t *dummy_ptr = (int64_t*)dummy;
  delete dummy_ptr;
}

void delete_dummyF(int64_t dummy) {
  float_t *dummy_ptr = (float_t*)dummy;
  delete dummy_ptr;
}

void delete_dummyD(int64_t dummy) {
  double_t *dummy_ptr = (double_t*)dummy;
  delete dummy_ptr;
}

int8_t ttreeGetRow(int64_t ttree, int64_t row) {
  TTree *ttree_ptr = (TTree*)ttree;  
  ttree_ptr->GetEntry(row);
  // TODO: if failure, return 0;
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

float_t getValueLeafF(int64_t dummy) {
  return *((float_t*)dummy);
}

double_t getValueLeafD(int64_t dummy) {
  return *((double_t*)dummy);
}

const char *getValueLeafC(int64_t tleaf) {
  TLeafC *tleaf_ptr = (TLeafC*)tleaf;
  return tleaf_ptr->GetValueString();
}
