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
  TString four;
  Float_t five[2];
  Float_t six[4];
  Float_t seven[2][2];
  TObjArray eight;

  tree->Branch("one", &one, "one/I");
  tree->Branch("two", &two, "two/F");
  tree->Branch("three", &three, "three/C");
  tree->Branch("four", &four, 16000, 0);
  tree->Branch("five", &five, "five[2]/F");
  tree->Branch("six", &six, "six[one]/F");
  tree->Branch("seven", &seven, "seven[2][2]/F");
  tree->Branch("eight", &eight, 16000, 0);

  one = 1;
  two = 1.1;
  strcpy(three, std::string("uno").c_str());
  four = "uno";
  five[0] = 3.14;
  five[1] = 6.28;
  six[0] = 1.1;
  seven[0][0] = 1.1;
  seven[0][1] = 0.0;
  seven[1][0] = 0.0;
  seven[1][1] = 1.1;
  eight.Add(new TObjString(four));
  tree->Fill();

  one = 2;
  two = 2.2;
  strcpy(three, std::string("dos").c_str());
  four = "dos";
  five[0] = -3.14;
  five[1] = -6.28;
  six[0] = 1.1;
  six[1] = 2.2;
  seven[0][0] = 2.2;
  seven[0][1] = 0.0;
  seven[1][0] = 0.0;
  seven[1][1] = 2.2;
  eight.Add(new TObjString(four));
  tree->Fill();

  one = 3;
  two = 3.3;
  strcpy(three, std::string("tres").c_str());
  four = "tres";
  five[0] = 1;
  five[1] = 2;
  six[0] = 1.1;
  six[1] = 2.2;
  six[2] = 3.3;
  seven[0][0] = 3.3;
  seven[0][1] = 0.0;
  seven[1][0] = 0.0;
  seven[1][1] = 3.3;
  eight.Add(new TObjString(four));
  tree->Fill();

  one = 4;
  two = 4.4;
  strcpy(three, std::string("quatro").c_str());
  four = "quatro";
  five[0] = -1;
  five[1] = -2;
  six[0] = 1.1;
  six[1] = 2.2;
  six[2] = 3.3;
  six[3] = 4.4;
  seven[0][0] = 4.4;
  seven[0][1] = 0.0;
  seven[1][0] = 0.0;
  seven[1][1] = 4.4;
  eight.Add(new TObjString(four));
  tree->Fill();

  f2->Write();
  f2->Close();

  // TFile *f2again = new TFile("simple.root");
  // TTree *treeagain;
  // f2again->GetObject("tree", treeagain);
  // treeagain->Scan();

  TFile *f3 = new TFile("notsosimple.root", "RECREATE");
  TTree *tree2 = new TTree("tree", "fake data");
  Double_t scalar;
  std::vector<Double_t> vector;
  std::vector<std::vector<Double_t> > tensor;
  tree2->Branch("scalar", &scalar);
  tree2->Branch("vector", &vector);
  tree2->Branch("tensor", &tensor);

  scalar = 3.14;
  vector = {1.1, 2.2, 3.3};
  tensor = {{0.9, 0.1}, {-0.1, 0.9}};
  tree2->Fill();

  f3->Write();
  f3->Close();
}
