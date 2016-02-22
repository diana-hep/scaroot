#include <iostream>

#include "RootTreeReader.h"

RootTreeReader::RootTreeReader(const char *fileLocation, const char *treeLocation) {
  std::cout << "building with " << fileLocation << " " << treeLocation << std::endl;
  x = 12;
}

int RootTreeReader::hello() {
  return x;
}
