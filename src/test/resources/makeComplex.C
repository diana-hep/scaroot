#include "stdio.h"
#include "Riostream.h"
#include <iostream>
#include <vector>

struct ministruct {
  int one;
  double two;
};

struct structure {
  char mybyte;
  unsigned char myubyte;
  short myshort;
  unsigned short myushort;
  int myint;
  unsigned int myuint;
  long mylong;
  unsigned long myulong;
  float myfloat;
  double mydouble;
  char myfixedstr[10];
  char *myfloatstr;
  std::string mystdstr;
  // ministruct mystruct;
  std::vector<ministruct> mystructs;
};

void makeComplex() {
  TFile *f = new TFile("complex.root", "RECREATE");
  TDirectory *dir = f->mkdir("subdir");
  dir->cd();

  char mybyte;
  unsigned char myubyte;
  short myshort;
  unsigned short myushort;
  int myint;
  unsigned int myuint;
  long mylong;
  unsigned long myulong;
  float myfloat;
  double mydouble;
  char myfixedstr[10];
  char *myfloatstr;
  std::string mystdstr;
  structure mystruct;

  char myarraybyte[2];
  unsigned char myarrayubyte[2];
  short myarrayshort[2];
  unsigned short myarrayushort[2];
  int myarrayint[2];
  unsigned int myarrayuint[2];
  long myarraylong[2];
  unsigned long myarrayulong[2];
  float myarrayfloat[2];
  double myarraydouble[2];
  std::string myarraystdstr[2];
  structure myarraystruct[2];

  std::vector<char> myvectorbyte;
  std::vector<unsigned char> myvectorubyte;
  std::vector<short> myvectorshort;
  std::vector<unsigned short> myvectorushort;
  std::vector<int> myvectorint;
  std::vector<unsigned int> myvectoruint;
  std::vector<long> myvectorlong;
  std::vector<unsigned long> myvectorulong;
  std::vector<float> myvectorfloat;
  std::vector<double> myvectordouble;
  std::vector<char*> myvectorfloatstr;
  std::vector<std::string> myvectorstdstr;
  std::vector<structure> myvectorstruct;

  mybyte = -3;
  myubyte = 3;
  myshort = -3;
  myushort = 3;
  myint = -3;
  myuint = 3;
  mylong = -3;
  myulong = 3;
  myfloat = -3.14;
  mydouble = 3.14;
  strcpy(myfixedstr, "hello");
  char there[10];
  strcpy(there, "there");
  myfloatstr = there;
  mystdstr = std::string("you");
  mystruct.mybyte = -3;
  mystruct.myubyte = 3;
  mystruct.myshort = -3;
  mystruct.myushort = 3;
  mystruct.myint = -3;
  mystruct.myuint = 3;
  mystruct.mylong = -3;
  mystruct.myulong = 3;
  mystruct.myfloat = -3.14;
  mystruct.mydouble = 3.14;
  strcpy(mystruct.myfixedstr, "hello");
  mystruct.myfloatstr = there;
  mystruct.mystdstr = std::string("you");
  // mystruct.mystruct.one = 1;
  // mystruct.mystruct.two = 2.2;
  ministruct tmp1;
  tmp1.one = 1;
  tmp1.two = 2.2;
  ministruct tmp2;
  tmp2.one = -1;
  tmp2.two = -2.2;
  mystruct.mystructs = {tmp1, tmp2};

  myarraybyte[0] = -3;
  myarraybyte[1] = 3;
  myarrayubyte[0] = 3;
  myarrayubyte[1] = 3;
  myarrayshort[0] = -3;
  myarrayshort[1] = 3;
  myarrayushort[0] = 3;
  myarrayushort[1] = 3;
  myarrayint[0] = -3;
  myarrayint[1] = -3;
  myarrayuint[0] = 3;
  myarrayuint[1] = 3;
  myarraylong[0] = -3;
  myarraylong[1] = -3;
  myarrayulong[0] = 3;
  myarrayulong[1] = 3;
  myarrayfloat[0] = -3.14;
  myarrayfloat[1] = 3.14;
  myarraydouble[0] = -3.14;
  myarraydouble[1] = 3.14;
  myarraystdstr[0] = std::string("you");
  myarraystdstr[1] = std::string("guys");
  myarraystruct[0].mybyte = -3;
  myarraystruct[0].myubyte = 3;
  myarraystruct[0].myshort = -3;
  myarraystruct[0].myushort = 3;
  myarraystruct[0].myint = -3;
  myarraystruct[0].myuint = 3;
  myarraystruct[0].mylong = -3;
  myarraystruct[0].myulong = 3;
  myarraystruct[0].myfloat = -3.14;
  myarraystruct[0].mydouble = 3.14;
  strcpy(myarraystruct[0].myfixedstr, "hello");
  myarraystruct[0].myfloatstr = there;
  myarraystruct[0].mystdstr = std::string("you");
  // myarraystruct[0].mystruct.one = 1;
  // myarraystruct[0].mystruct.two = 2.2;
  myarraystruct[0].mystructs = {tmp1, tmp2};
  myarraystruct[1].mybyte = 3;
  myarraystruct[1].myubyte = 3;
  myarraystruct[1].myshort = 3;
  myarraystruct[1].myushort = 3;
  myarraystruct[1].myint = 3;
  myarraystruct[1].myuint = 3;
  myarraystruct[1].mylong = 3;
  myarraystruct[1].myulong = 3;
  myarraystruct[1].myfloat = 3.14;
  myarraystruct[1].mydouble = 3.14;
  strcpy(myarraystruct[1].myfixedstr, "hello");
  myarraystruct[1].myfloatstr = there;
  myarraystruct[1].mystdstr = std::string("you");
  // myarraystruct[1].mystruct.one = 1;
  // myarraystruct[1].mystruct.two = 2.2;
  myarraystruct[1].mystructs = {tmp2, tmp1};

  myvectorbyte = {3, -3};
  myvectorubyte = {3, 3};
  myvectorshort = {3, -3};
  myvectorushort = {3, 3};
  myvectorint = {3, -3};
  myvectoruint = {3, 3};
  myvectorlong = {3, -3};
  myvectorulong = {3, 3};
  myvectorfloat = {3.14, -3.14};
  myvectordouble = {3.14, -3.14};
  myvectorfloatstr = {there, there};
  myvectorstdstr = {std::string("uno"), std::string("dos")};
  myvectorstruct = {myarraystruct[1], myarraystruct[0]};

  std::cout << "mybyte: " << (int)mybyte << std::endl;
  std::cout << "myubyte: " << (unsigned int)myubyte << std::endl;
  std::cout << "myshort: " << myshort << std::endl;
  std::cout << "myushort: " << myushort << std::endl;
  std::cout << "myint: " << myint << std::endl;
  std::cout << "myuint: " << myuint << std::endl;
  std::cout << "mylong: " << mylong << std::endl;
  std::cout << "myulong: " << myulong << std::endl;
  std::cout << "myfloat: " << myfloat << std::endl;
  std::cout << "mydouble: " << mydouble << std::endl;
  std::cout << "myfixedstr: " << myfixedstr << std::endl;
  std::cout << "myfloatstr: " << myfloatstr << std::endl;
  std::cout << "mystdstr: " << mystdstr << std::endl;
  std::cout << "mystruct: " <<
    (int)mystruct.mybyte << " " <<
    (unsigned int)mystruct.myubyte << " " <<
    mystruct.myshort << " " <<
    mystruct.myushort << " " <<
    mystruct.myint << " " <<
    mystruct.myuint << " " <<
    mystruct.mylong << " " <<
    mystruct.myulong << " " <<
    mystruct.myfloat << " " <<
    mystruct.mydouble << " " <<
    mystruct.myfixedstr << " " <<
    mystruct.myfloatstr << " " <<
    mystruct.mystdstr << " " <<
    // "{" << mystruct.mystruct.one << ", " << mystruct.mystruct.two << "} "
    "[{" << mystruct.mystructs[0].one << ", " << mystruct.mystructs[0].two << "}, {" << mystruct.mystructs[1].one << ", " << mystruct.mystructs[1].two << "}]" << std::endl;

  std::cout << "myarraybyte: " << (int)myarraybyte[0] << " " << (int)myarraybyte[1] << std::endl;
  std::cout << "myarrayubyte: " << (unsigned int)myarrayubyte[0] << " " << (unsigned int)myarrayubyte[1] << std::endl;
  std::cout << "myarrayshort: " << myarrayshort[0] << " " << myarrayshort[1] << std::endl;
  std::cout << "myarrayushort: " << myarrayushort[0] << " " << myarrayushort[1] << std::endl;
  std::cout << "myarrayint: " << myarrayint[0] << " " << myarrayint[1] << std::endl;
  std::cout << "myarrayuint: " << myarrayuint[0] << " " << myarrayuint[1] << std::endl;
  std::cout << "myarraylong: " << myarraylong[0] << " " << myarraylong[1] << std::endl;
  std::cout << "myarrayulong: " << myarrayulong[0] << " " << myarrayulong[1] << std::endl;
  std::cout << "myarrayfloat: " << myarrayfloat[0] << " " << myarrayfloat[1] << std::endl;
  std::cout << "myarraydouble: " << myarraydouble[0] << " " << myarraydouble[1] << std::endl;
  std::cout << "myarraystdstr: " << myarraystdstr[0] << " " << myarraystdstr[1] << std::endl;
  std::cout << "myarraystruct[0]: " <<
    (int)myarraystruct[0].mybyte << " " <<
    (unsigned int)myarraystruct[0].myubyte << " " <<
    myarraystruct[0].myshort << " " <<
    myarraystruct[0].myushort << " " <<
    myarraystruct[0].myint << " " <<
    myarraystruct[0].myuint << " " <<
    myarraystruct[0].mylong << " " <<
    myarraystruct[0].myulong << " " <<
    myarraystruct[0].myfloat << " " <<
    myarraystruct[0].mydouble << " " <<
    myarraystruct[0].myfixedstr << " " <<
    myarraystruct[0].myfloatstr << " " <<
    myarraystruct[0].mystdstr << " " <<
    // "{" << myarraystruct[0].mystruct.one << ", " << myarraystruct[0].mystruct.two << "} "
    "[{" << myarraystruct[0].mystructs[0].one << ", " << myarraystruct[0].mystructs[0].two << "}, {" << myarraystruct[0].mystructs[1].one << ", " << myarraystruct[0].mystructs[1].two << "}]" << std::endl;
  std::cout << "myarraystruct[1]: " <<
    (int)myarraystruct[1].mybyte << " " <<
    (unsigned int)myarraystruct[1].myubyte << " " <<
    myarraystruct[1].myshort << " " <<
    myarraystruct[1].myushort << " " <<
    myarraystruct[1].myint << " " <<
    myarraystruct[1].myuint << " " <<
    myarraystruct[1].mylong << " " <<
    myarraystruct[1].myulong << " " <<
    myarraystruct[1].myfloat << " " <<
    myarraystruct[1].mydouble << " " <<
    myarraystruct[1].myfixedstr << " " <<
    myarraystruct[1].myfloatstr << " " <<
    myarraystruct[1].mystdstr << " " <<
    // "{" << myarraystruct[1].mystruct.one << ", " << myarraystruct[1].mystruct.two << "} "
    "[{" << myarraystruct[1].mystructs[0].one << ", " << myarraystruct[1].mystructs[0].two << "}, {" << myarraystruct[1].mystructs[1].one << ", " << myarraystruct[1].mystructs[1].two << "}]" << std::endl;

  std::cout << "myvectorbyte: " << (int)myvectorbyte[0] << " " << (int)myvectorbyte[1] << std::endl;
  std::cout << "myvectorubyte: " << (unsigned int)myvectorubyte[0] << " " << (unsigned int)myvectorubyte[1] << std::endl;
  std::cout << "myvectorshort: " << myvectorshort[0] << " " << myvectorshort[1] << std::endl;
  std::cout << "myvectorushort: " << myvectorushort[0] << " " << myvectorushort[1] << std::endl;
  std::cout << "myvectorint: " << myvectorint[0] << " " << myvectorint[1] << std::endl;
  std::cout << "myvectoruint: " << myvectoruint[0] << " " << myvectoruint[1] << std::endl;
  std::cout << "myvectorlong: " << myvectorlong[0] << " " << myvectorlong[1] << std::endl;
  std::cout << "myvectorulong: " << myvectorulong[0] << " " << myvectorulong[1] << std::endl;
  std::cout << "myvectorfloat: " << myvectorfloat[0] << " " << myvectorfloat[1] << std::endl;
  std::cout << "myvectordouble: " << myvectordouble[0] << " " << myvectordouble[1] << std::endl;
  std::cout << "myvectorstdstr: " << myvectorstdstr[0] << " " << myvectorstdstr[1] << std::endl;
  std::cout << "myvectorstruct[0]: " <<
    (int)myvectorstruct[0].mybyte << " " <<
    (unsigned int)myvectorstruct[0].myubyte << " " <<
    myvectorstruct[0].myshort << " " <<
    myvectorstruct[0].myushort << " " <<
    myvectorstruct[0].myint << " " <<
    myvectorstruct[0].myuint << " " <<
    myvectorstruct[0].mylong << " " <<
    myvectorstruct[0].myulong << " " <<
    myvectorstruct[0].myfloat << " " <<
    myvectorstruct[0].mydouble << " " <<
    myvectorstruct[0].myfixedstr << " " <<
    myvectorstruct[0].myfloatstr << " " <<
    myvectorstruct[0].mystdstr << " " <<
    // "{" << myvectorstruct[0].mystruct.one << ", " << myvectorstruct[0].mystruct.two << "} "
    "[{" << myvectorstruct[0].mystructs[0].one << ", " << myvectorstruct[0].mystructs[0].two << "}, {" << myvectorstruct[0].mystructs[1].one << ", " << myvectorstruct[0].mystructs[1].two << "}]" << std::endl;
  std::cout << "myvectorstruct[1]: " <<
    (int)myvectorstruct[1].mybyte << " " <<
    (unsigned int)myvectorstruct[1].myubyte << " " <<
    myvectorstruct[1].myshort << " " <<
    myvectorstruct[1].myushort << " " <<
    myvectorstruct[1].myint << " " <<
    myvectorstruct[1].myuint << " " <<
    myvectorstruct[1].mylong << " " <<
    myvectorstruct[1].myulong << " " <<
    myvectorstruct[1].myfloat << " " <<
    myvectorstruct[1].mydouble << " " <<
    myvectorstruct[1].myfixedstr << " " <<
    myvectorstruct[1].myfloatstr << " " <<
    myvectorstruct[1].mystdstr << " " <<
    // "{" << myvectorstruct[1].mystruct.one << ", " << myvectorstruct[1].mystruct.two << "} "
    "[{" << myvectorstruct[1].mystructs[0].one << ", " << myvectorstruct[1].mystructs[0].two << "}, {" << myvectorstruct[1].mystructs[1].one << ", " << myvectorstruct[1].mystructs[1].two << "}]" << std::endl;

  TTree *tree0 = new TTree("tree0", "");
  TTree *tree1 = new TTree("tree1", "");
  TTree *tree2 = new TTree("tree2", "");

  std::vector<TTree*> trees;
  trees.push_back(tree0);
  trees.push_back(tree1);
  trees.push_back(tree2);

  for (TTree *t : trees) {
    t->Branch("mybyte", &mybyte);
    t->Branch("myubyte", &myubyte);
    t->Branch("myshort", &myshort);
    t->Branch("myushort", &myushort);
    t->Branch("myint", &myint);
    t->Branch("myuint", &myuint);
    // t->Branch("mylong", &mylong);
    // t->Branch("myulong", &myulong);
    t->Branch("myfloat", &myfloat);
    t->Branch("mydouble", &mydouble);
    // t->Branch("myfixedstr", &myfixedstr);
    // t->Branch("myfloatstr", &myfloatstr);
    t->Branch("mystdstr", &mystdstr);
    t->Branch("mystruct", &mystruct);

    // t->Branch("myarraybyte", &myarraybyte);
    // t->Branch("myarrayubyte", &myarrayubyte);
    // t->Branch("myarrayshort", &myarrayshort);
    // t->Branch("myarrayushort", &myarrayushort);
    // t->Branch("myarrayint", &myarrayint);
    // t->Branch("myarrayuint", &myarrayuint);
    // t->Branch("myarraylong", &myarraylong);
    // t->Branch("myarrayulong", &myarrayulong);
    // t->Branch("myarrayfloat", &myarrayfloat);
    // t->Branch("myarraydouble", &myarraydouble);
    // t->Branch("myarraystdstr", &myarraystdstr);
    // t->Branch("myarraystruct", &myarraystruct);

    t->Branch("myvectorbyte", &myvectorbyte);
    t->Branch("myvectorubyte", &myvectorubyte);
    t->Branch("myvectorshort", &myvectorshort);
    t->Branch("myvectorushort", &myvectorushort);
    t->Branch("myvectorint", &myvectorint);
    t->Branch("myvectoruint", &myvectoruint);
    t->Branch("myvectorlong", &myvectorlong);
    t->Branch("myvectorulong", &myvectorulong);
    t->Branch("myvectorfloat", &myvectorfloat);
    t->Branch("myvectordouble", &myvectordouble);
    t->Branch("myvectorfloatstr", &myvectorfloatstr);
    t->Branch("myvectorstdstr", &myvectorstdstr);
    // t->Branch("myvectorstruct", &myvectorstruct);
  }

  tree1->Fill();

  tree2->Fill();
  tree2->Fill();

  f->Write();
  f->Close();
}
