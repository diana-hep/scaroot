void checkComplex() {
  TFile *f = new TFile("complex.root");
  TDirectory *d;
  f->GetObject("subdir", d);

  TTree *tree0;
  d->GetObject("tree0", tree0);
  TTree *tree1;
  d->GetObject("tree1", tree1);
  TTree *tree2;
  d->GetObject("tree2", tree2);

  std::cout << "tree0: " << tree0->GetEntries() << std::endl;
  tree0->Scan("mybyte:myubyte:myshort:myushort:myint:myuint:myfloat:mydouble:mystdstr");
  tree0->Scan("mystruct.mybyte:mystruct.myubyte:mystruct.myshort:mystruct.myushort:mystruct.myint:mystruct.myuint:mystruct.mylong:mystruct.myulong:mystruct.myfloat:mystruct.mydouble:mystruct.myfixedstr:mystruct.myfloatstr:mystruct.mystdstr");
  tree0->Scan("mystruct.mystructs[].one");
  tree0->Scan("mystruct.mystructs[].two");
  tree0->Scan("myvectorbyte:myvectorubyte:myvectorshort:myvectorushort:myvectorint:myvectoruint:myvectorlong:myvectorulong:myvectorfloat:myvectordouble:myvectorfloatstr:myvectorstdstr");

  std::cout << "tree1: " << tree1->GetEntries() << std::endl;
  tree1->Scan("mybyte:myubyte:myshort:myushort:myint:myuint:myfloat:mydouble:mystdstr");
  tree1->Scan("mystruct.mybyte:mystruct.myubyte:mystruct.myshort:mystruct.myushort:mystruct.myint:mystruct.myuint:mystruct.mylong:mystruct.myulong:mystruct.myfloat:mystruct.mydouble:mystruct.myfixedstr:mystruct.myfloatstr:mystruct.mystdstr");
  tree1->Scan("mystruct.mystructs[].one");
  tree1->Scan("mystruct.mystructs[].two");
  tree1->Scan("myvectorbyte:myvectorubyte:myvectorshort:myvectorushort:myvectorint:myvectoruint:myvectorlong:myvectorulong:myvectorfloat:myvectordouble:myvectorfloatstr:myvectorstdstr");

  std::cout << "tree2: " << tree2->GetEntries() << std::endl;
  tree2->Scan("mybyte:myubyte:myshort:myushort:myint:myuint:myfloat:mydouble:mystdstr");
  tree2->Scan("mystruct.mybyte:mystruct.myubyte:mystruct.myshort:mystruct.myushort:mystruct.myint:mystruct.myuint:mystruct.mylong:mystruct.myulong:mystruct.myfloat:mystruct.mydouble:mystruct.myfixedstr:mystruct.myfloatstr:mystruct.mystdstr");
  tree2->Scan("mystruct.mystructs[].one");
  tree2->Scan("mystruct.mystructs[].two");
  tree2->Scan("myvectorbyte:myvectorubyte:myvectorshort:myvectorushort:myvectorint:myvectoruint:myvectorlong:myvectorulong:myvectorfloat:myvectordouble:myvectorfloatstr:myvectorstdstr");
}
