#include <iostream>

// #include <TROOT.h>
// #include <TInterpreter.h>

#include "RootTreeReader.h"

RootTreeReader::RootTreeReader(const char *fileLocation, const char *treeLocation) {
  std::cout << "START" << std::endl;
  tfile = TFile::Open(fileLocation);
  std::cout << "got file" << std::endl;
  reader = new TTreeReader(treeLocation, tfile);
  std::cout << "got reader" << std::endl;
  std::cout << "FINISH" << std::endl;
}

// int main(int argc, char **argv) {
//   RootTreeReader r = RootTreeReader("../../../test-examples/verysimple.root", "ntuple");
// }


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
