#include <iostream>

// #include <TROOT.h>
// #include <TInterpreter.h>

#include "RootTreeReader.h"

TTreeReaderValue<float>* newValue_float(TTreeReader &reader, const char *name) {
  return new TTreeReaderValue<float>(reader, name);
}

RootTreeReader::RootTreeReader() {   // (const char *fileLocation, const char *treeLocation, int size, const char **names, const char **types) {
  // tfile = TFile::Open(fileLocation);
  // reader = new TTreeReader(treeLocation, tfile);
  // values = new ROOT::Internal::TTreeReaderValueBase*[size];
  // for (int i = 0;  i < size;  i++) {
  //   if (std::string(types[i]) == std::string("float"))
  //     values[i] = newValue_float(*reader, names[i]);
  // }

  tfile = TFile::Open("root://cmsxrootd.fnal.gov//store/user/pivarski/TrackResonanceNtuple.root");
  reader = new TTreeReader("TrackResonanceNtuple/twoMuon", tfile);
  values = new ROOT::Internal::TTreeReaderValueBase*[4];
  values[0] = newValue_float(*reader, "mass_mumu");
  values[1] = newValue_float(*reader, "px");
  values[2] = newValue_float(*reader, "py");
  values[3] = newValue_float(*reader, "pz");
}

bool RootTreeReader::next() {
  return reader->Next();
}

float RootTreeReader::get_float(int index) {
  return *((float*)values[index]->GetAddress());
}

// int main(int argc, char **argv) {
//   std::string mass_mumu("mass_mumu");
//   std::string px("px");
//   std::string py("py");
//   std::string pz("pz");
//   std::string f("float");
//   const char *names[] = {mass_mumu.c_str(), px.c_str(), py.c_str(), pz.c_str()};
//   const char *types[] = {f.c_str(), f.c_str(), f.c_str(), f.c_str()};
//   RootTreeReader r("root://cmsxrootd.fnal.gov//store/user/pivarski/TrackResonanceNtuple.root", "TrackResonanceNtuple/twoMuon", 4, names, types);
//   std::cout << "start" << std::endl;
//   while (r.next()) {
//     std::cout << "    " << r.get_float(0) << " " << r.get_float(1) << " " << r.get_float(2) << " " << r.get_float(3) << std::endl;
//   }
//   std::cout << "end" << std::endl;
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
