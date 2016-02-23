#include <string>

#include "RootTreeReader.h"

int test1(int value) {
  return value + 100;
}

TFile *test2() {
  return new TFile("/home/pivarski/diana-github/test-examples/verysimple.root");
}
