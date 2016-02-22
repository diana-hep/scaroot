#include <iostream>

// #include <TROOT.h>
// #include <TInterpreter.h>

#include "RootTreeReader.h"

static TTreeReaderValue<float> *mass_mumu, *px, *py, *pz;

TTreeReaderValue<float>* newValue_float(TTreeReader &reader, const char *name) {
  return new TTreeReaderValue<float>(reader, name);
}

RootTreeReader::RootTreeReader(const char *fileLocation, const char *treeLocation, int size, const char **names, const char **types) {
  tfile = TFile::Open(fileLocation);
  reader = new TTreeReader(treeLocation, tfile);

  // values = new ROOT::Internal::TTreeReaderValueBase*[size];
  // for (int i = 0;  i < size;  i++) {
  //   if (std::string(types[i]) == std::string("float"))
  //     values[i] = newValue_float(*reader, names[i]);
  // }

  mass_mumu = new TTreeReaderValue<float>(*reader, "mass_mumu");
  px = new TTreeReaderValue<float>(*reader, "px");
  py = new TTreeReaderValue<float>(*reader, "py");
  pz = new TTreeReaderValue<float>(*reader, "pz");
}

bool RootTreeReader::next() {
  return reader->Next();
}

// void *RootTreeReader::get(int index) {
//   return values[index]->GetAddress();
// }

float RootTreeReader::get(int index) {
  switch (index) {
  case 0:
    return *mass_mumu->Get();
  case 1:
    return *px->Get();
  case 2:
    return *py->Get();
  case 3:
    return *pz->Get();
  }
}

//   auto code = std::string("#include \"Event.h\"\n") +
//     std::string("void run() {\n") +
//     std::string("  TFile fileIn(\"/home/pivarski/tmp/freehep_tests/Event.root\");\n") +
//     std::string("  TTreeReader theReader(\"T\", &fileIn);\n") +
//     std::string("  TTreeReaderValue<Event> eventRV(theReader, \"event\");\n") +
//     std::string("  TTreeReaderValue<Int_t> nTracksRV(theReader, \"fNtrack\");\n") +
//     std::string("  while (theReader.Next()) {\n") +
//     std::string("    auto event = eventRV.Get();\n") +
//     std::string("    std::cout << *nTracksRV << \" \" << event->GetNseg() << \" \" << event->GetTemperature() << std::endl;\n") +
//     std::string("  }\n") +
//     std::string("}\n");

//   gROOT->ProcessLine(".L Event_cxx.so");

//   TInterpreter *tInterpreter = gROOT->GetInterpreter();
//   tInterpreter->Declare(code.c_str());

//   gROOT->ProcessLine("run();");
