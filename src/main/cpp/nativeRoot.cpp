#include <stdint.h>

#include "TFile.h"

extern "C" {
  int64_t new_TFile(std::string rootFileLocation);
  void close_TFile(int64_t tfile);
  void delete_TFile(int64_t tfile);
  int64_t getTTree(int64_t tfile, std::string ttreeLocation);

  int64_t ttreeGetNumEntries(int64_t ttree);
  int64_t ttreeGetNumLeaves(int64_t ttree);
  int64_t ttreeGetLeaf(int64_t ttree, int64_t i);
  std::string ttreeGetLeafName(int64_t ttree, int64_t i);
  std::string ttreeGetLeafType(int64_t ttree, int64_t i);

  int8_t getValueLeafB(int64_t leaf, int64_t row);
  int16_t getValueLeafS(int64_t leaf, int64_t row);
  int32_t getValueLeafI(int64_t leaf, int64_t row);
  int64_t getValueLeafL(int64_t leaf, int64_t row);
  float getValueLeafF(int64_t leaf, int64_t row);
  double_t getValueLeafD(int64_t leaf, int64_t row);
  std::string getValueLeafC(int64_t leaf, int64_t row);
}

int64_t new_TFile(std::string rootFileLocation) {
  return 0;
}

void close_TFile(int64_t tfile) {

}

void delete_TFile(int64_t tfile) {

}

int64_t getTTree(int64_t tfile, std::string ttreeLocation) {
  return 0;
}

int64_t ttreeGetNumEntries(int64_t ttree) {
  return 0;
}

int64_t ttreeGetNumLeaves(int64_t ttree) {
  return 0;
}

int64_t ttreeGetLeaf(int64_t ttree, int64_t i) {
  return 0;
}

std::string ttreeGetLeafName(int64_t ttree, int64_t i) {
  return std::string("");
}

std::string ttreeGetLeafType(int64_t ttree, int64_t i) {
  return std::string("");
}

int8_t getValueLeafB(int64_t leaf, int64_t row) {
  return 0;
}

int16_t getValueLeafS(int64_t leaf, int64_t row) {
  return 0;
}

int32_t getValueLeafI(int64_t leaf, int64_t row) {
  return 0;
}

int64_t getValueLeafL(int64_t leaf, int64_t row) {
  return 0;
}

float getValueLeafF(int64_t leaf, int64_t row) {
  return 0;
}

double getValueLeafD(int64_t leaf, int64_t row) {
  return 0;
}

std::string getValueLeafC(int64_t leaf, int64_t row) {
  return std::string("");
}

// int64_t new_TFile(char *fileName) {
//   TFile *tfile = new TFile(fileName);
//   return (int64_t)tfile;
// }

// void delete_TFile(int64_t pointer) {
//   TFile *tfile = (TFile*)pointer;
//   delete tfile;
// }

// void TFile_ls(int64_t pointer) {
//   TFile *tfile = (TFile*)pointer;
//   tfile->ls();
// }
