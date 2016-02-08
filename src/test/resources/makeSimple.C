#include "Riostream.h"

void makeSimple() {
  TFile *f = new TFile("verysimple.root", "RECREATE");
  TNtuple *ntuple = new TNtuple("ntuple", "fake data", "x:y:z");

  ntuple->Fill(1, 2, 3);
  ntuple->Fill(4, 5, 6);
  ntuple->Fill(7, 8, 9);
  ntuple->Fill(10, 11, 12);
  ntuple->Fill(1, 2, 3);

  f->Write();
  f->Close();

  TFile *f2 = new TFile("simple.root", "RECREATE");
  TTree *tree = new TTree("tree", "fake data");
  Int_t one;
  Float_t two;
  Char_t three[10];
  tree->Branch("one", &one, "one/I");
  tree->Branch("two", &two, "two/F");
  tree->Branch("three", &three, "three/C");

  one = 1;
  two = 1.1;
  strcpy(three, std::string("uno").c_str());
  tree->Fill();

  one = 2;
  two = 2.2;
  strcpy(three, std::string("dos").c_str());
  tree->Fill();

  one = 3;
  two = 3.3;
  strcpy(three, std::string("tres").c_str());
  tree->Fill();

  one = 4;
  two = 4.4;
  strcpy(three, std::string("quatro").c_str());
  tree->Fill();

  f2->Write();
  f2->Close();
}
