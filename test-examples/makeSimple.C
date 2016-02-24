#include <string>

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
  TString *fourfour = new TString;
  std::string four;
  Float_t five[2];
  Float_t six[4];
  Float_t seven[2][2];
  Float_t sevenseven[4][4];
  TObjArray eight;

  tree->Branch("one", &one, "one/I");
  tree->Branch("two", &two, "two/F");
  tree->Branch("three", &three, "three/C");
  tree->Branch("fourfour", "TString", &fourfour);
  tree->Branch("four", &four);
  tree->Branch("five", &five, "five[2]/F");
  tree->Branch("six", &six, "six[one]/F");
  tree->Branch("seven", &seven, "seven[2][2]/F");
  tree->Branch("sevenseven", &sevenseven, "sevenseven[one][one]/F");
  TBranch *check = tree->Branch("eight", &eight, 16000, 0);

  // std::cout << "check " << (((TBranchElement*)check)->GetType()) << " " << TBranchElement::kSTLNode << std::endl;

  one = 1;
  two = 1.1;
  strcpy(three, std::string("uno").c_str());
  fourfour->Clear();
  fourfour->Append("uno");
  four.assign("uno");
  five[0] = 3.14;
  five[1] = 6.28;
  six[0] = 1.1;
  seven[0][0] = 1.1;
  seven[0][1] = 0.0;
  seven[1][0] = 0.0;
  seven[1][1] = 1.1;
  sevenseven[0][0] = 3.14;
  eight.Add(new TObjString("uno"));
  tree->Fill();

  one = 2;
  two = 2.2;
  strcpy(three, std::string("dos").c_str());
  fourfour->Clear();
  fourfour->Append("dos");
  four.assign("dos");
  five[0] = -3.14;
  five[1] = -6.28;
  six[0] = 1.1;
  six[1] = 2.2;
  seven[0][0] = 2.2;
  seven[0][1] = 0.0;
  seven[1][0] = 0.0;
  seven[1][1] = 2.2;
  sevenseven[0][0] = 3.14;
  sevenseven[0][1] = 3.14;
  sevenseven[1][0] = 3.14;
  sevenseven[1][1] = 3.14;
  eight.Add(new TObjString("dos"));
  tree->Fill();

  one = 3;
  two = 3.3;
  strcpy(three, std::string("tres").c_str());
  fourfour->Clear();
  fourfour->Append("tres");
  four.assign("tres");
  five[0] = 1;
  five[1] = 2;
  six[0] = 1.1;
  six[1] = 2.2;
  six[2] = 3.3;
  seven[0][0] = 3.3;
  seven[0][1] = 0.0;
  seven[1][0] = 0.0;
  seven[1][1] = 3.3;
  sevenseven[0][0] = 3.14;
  sevenseven[0][1] = 3.14;
  sevenseven[0][2] = 3.14;
  sevenseven[1][0] = 3.14;
  sevenseven[1][1] = 3.14;
  sevenseven[1][2] = 3.14;
  sevenseven[2][0] = 3.14;
  sevenseven[2][1] = 3.14;
  sevenseven[2][2] = 3.14;
  eight.Add(new TObjString("tres"));
  tree->Fill();

  one = 4;
  two = 4.4;
  strcpy(three, std::string("quatro").c_str());
  fourfour->Clear();
  fourfour->Append("quatro");
  four.assign("quatro");
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
  sevenseven[0][0] = 3.14;
  sevenseven[0][1] = 3.14;
  sevenseven[0][2] = 3.14;
  sevenseven[0][3] = 3.14;
  sevenseven[1][0] = 3.14;
  sevenseven[1][1] = 3.14;
  sevenseven[1][2] = 3.14;
  sevenseven[1][3] = 3.14;
  sevenseven[2][0] = 3.14;
  sevenseven[2][1] = 3.14;
  sevenseven[2][2] = 3.14;
  sevenseven[2][3] = 3.14;
  sevenseven[3][0] = 3.14;
  sevenseven[3][1] = 3.14;
  sevenseven[3][2] = 3.14;
  sevenseven[3][3] = 3.14;
  eight.Add(new TObjString("quatro"));
  tree->Fill();

  f2->Write();
  f2->Close();

  TFile *f2again = new TFile("simple.root");
  TTree *treeagain;
  f2again->GetObject("tree", treeagain);
  treeagain->Scan("sevenseven");

  TFile *f3 = new TFile("notsosimple.root", "RECREATE");
  TTree *tree2 = new TTree("tree", "fake data");
  Double_t scalar;
  std::vector<Double_t> vector;
  std::vector<std::string> vector2;
  std::vector<std::vector<Double_t> > tensor;
  // std::vector<std::vector<std::vector<Double_t> > > rank3;
  // std::vector<std::vector<std::string> > tensor2;
  tree2->Branch("scalar", &scalar);
  tree2->Branch("vekkktor", &vector);
  tree2->Branch("vekkktor2", &vector2);
  tree2->Branch("tensor", &tensor);
  // tree2->Branch("tensor2", &tensor2);
  // tree2->Branch("rank3", &rank3);

  scalar = 3.14;
  vector = {1.1, 2.2, 3.3};
  vector2 = {std::string("one"), std::string("two"), std::string("three")};
  tensor = {{0.9, 0.1}, {-0.1, 0.9}};
  // tensor2 = {{std::string("a"), std::string("b")}, {std::string("c"), std::string("d")}};
  // rank3 = {{}, {{0.9, 0.1}}, {{0.9, 0.1}, {-0.1, 0.9}}};
  tree2->Fill();

  f3->Write();
  f3->Close();

  // TFile *f3again = new TFile("notsosimple.root");
  // TTree *treeagain;
  // f3again->GetObject("tree", treeagain);
  // treeagain->Scan();
}
