#include <stdio.h>

#include <TFile.h>
#include <TTreeReader.h>
#include <TTreeReaderValue.h>
#include <TTreeReaderArray.h>

#include <avro.h>

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

int main(int argc, char **argv) {
  printf("BEGIN\n");

  TFile *tfile = TFile::Open("../../test/resources/complex.root");
  TTreeReader ttreeReader("subdir/tree2", tfile);

  TTreeReaderValue<char> mybyte(ttreeReader, "mybyte");
  TTreeReaderValue<unsigned char> myubyte(ttreeReader, "myubyte");
  TTreeReaderValue<short> myshort(ttreeReader, "myshort");
  TTreeReaderValue<unsigned short> myushort(ttreeReader, "myushort");
  TTreeReaderValue<int> myint(ttreeReader, "myint");
  TTreeReaderValue<unsigned int> myuint(ttreeReader, "myuint");
  TTreeReaderValue<float> myfloat(ttreeReader, "myfloat");
  TTreeReaderValue<double> mydouble(ttreeReader, "mydouble");
  // TTreeReaderValue<std::string> mystdstr(ttreeReader, "mystdstr");
  TTreeReaderValue<structure> mystruct(ttreeReader, "mystruct");
  TTreeReaderArray<char> myvectorbyte(ttreeReader, "myvectorbyte");
  TTreeReaderArray<unsigned char> myvectorubyte(ttreeReader, "myvectorubyte");
  TTreeReaderArray<short> myvectorshort(ttreeReader, "myvectorshort");
  TTreeReaderArray<unsigned short> myvectorushort(ttreeReader, "myvectorushort");
  TTreeReaderArray<int> myvectorint(ttreeReader, "myvectorint");
  TTreeReaderArray<unsigned int> myvectoruint(ttreeReader, "myvectoruint");
  TTreeReaderArray<long> myvectorlong(ttreeReader, "myvectorlong");
  TTreeReaderArray<unsigned long> myvectorulong(ttreeReader, "myvectorulong");
  TTreeReaderArray<float> myvectorfloat(ttreeReader, "myvectorfloat");
  TTreeReaderArray<double> myvectordouble(ttreeReader, "myvectordouble");
  TTreeReaderArray<char> myvectorfloatstr(ttreeReader, "myvectorfloatstr");
  TTreeReaderArray<std::string> myvectorstdstr(ttreeReader, "myvectorstdstr");

  while (ttreeReader.Next()) {
    printf("mybyte: %d\n", (int)(*mybyte));
    printf("myubyte: %d\n", (int)(*myubyte));
    printf("myshort: %d\n", *myshort);
    printf("myushort: %d\n", *myushort);
    printf("myint: %d\n", *myint);
    printf("myuint: %d\n", *myuint);
    printf("myfloat: %g\n", *myfloat);
    printf("mydouble: %g\n", *mydouble);
    // printf("mystdstr: %s\n", *mystdstr);
    printf("mystruct.mybyte: %d\n", (int)(mystruct->mybyte));
    printf("mystruct.myubyte: %d\n", (int)(mystruct->myubyte));
    printf("mystruct.myshort: %d\n", mystruct->myshort);
    printf("mystruct.myushort: %d\n", mystruct->myushort);
    printf("mystruct.myint: %d\n", mystruct->myint);
    printf("mystruct.myuint: %d\n", mystruct->myuint);
    printf("mystruct.myfloat: %g\n", mystruct->myfloat);
    printf("mystruct.mydouble: %g\n", mystruct->mydouble);
    // printf("mystruct: %d\n", *mystruct);
    for (int i = 0;  i < 2;  ++i) {
      printf("myvectorbyte[%d]: %d\n", i, myvectorbyte[i]);
      printf("myvectorubyte[%d]: %d\n", i, myvectorubyte[i]);
      printf("myvectorshort[%d]: %d\n", i, myvectorshort[i]);
      printf("myvectorushort[%d]: %d\n", i, myvectorushort[i]);
      printf("myvectorint[%d]: %d\n", i, myvectorint[i]);
      printf("myvectoruint[%d]: %d\n", i, myvectoruint[i]);
      printf("myvectorlong[%d]: %ld\n", i, myvectorlong[i]);
      printf("myvectorulong[%d]: %ld\n", i, myvectorulong[i]);
      printf("myvectorfloat[%d]: %g\n", i, myvectorfloat[i]);
      printf("myvectordouble[%d]: %g\n", i, myvectordouble[i]);
      // printf("myvectorfloatstr[%d]: %s\n", i, myvectorfloatstr[i]);
      // printf("myvectorstdstr[%d]: %s\n", i, myvectorstdstr[i]);
    }
    printf("\n");
  }

  printf("END\n");
}
