#include <stdio.h>
#include <iostream>

#include <TROOT.h>
#include <TInterpreter.h>
#include <TFile.h>
#include <TTreeReader.h>
#include <TTreeReaderValue.h>
#include <TTreeReaderArray.h>

// class RootTreeReader {
//   public:
//     RootTreeReader(char *tfileLocation, char *ttreeLocation);
//     int hello();
// }

// RootTreeReader::RootTreeReader(char *tfileLocation, char *ttreeLocation) {
//   std::cout << "HEY " << tfileLocation << " " << ttreeLocation << std::endl;
// }

// int RootTreeReader::hello() {
//   return 321;
// }

int hello() {
  std::cout << "HEY" << std::endl;
  return 123;
}




// int main(int argc, char **argv) {
//   // std::cout << "BEGIN" << std::endl;

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

//   // std::cout << "END" << std::endl;
// }
