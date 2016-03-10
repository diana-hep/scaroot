package test.scala.scaroot

import scala.collection.mutable
import scala.language.postfixOps

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import com.sun.jna.Pointer

import org.dianahep.scaroot._

class DefaultSuite extends FlatSpec with Matchers {
  "ROOT in Scala" must "compute boolean expressions" in {
    trait Test {
      def one: Boolean
      def two(x: Boolean): Boolean
      def three(x: Boolean, y: Boolean): Boolean
    }
    val factory = RootClass.newClass[Test]("""
class Test {
public:
  bool one() { return true; }
  bool two(bool x) { return !x; }
  bool three(bool x, bool y) { return x && y; }
};
""")
    val instance = factory.newInstance
    instance.one should be (true)
    instance.two(true) should be (false)
    instance.two(false) should be (true)
    instance.three(true, true) should be (true)
    instance.three(true, false) should be (false)
    instance.three(false, false) should be (false)
    instance.three(false, false) should be (false)
  }

  it must "compute byte expressions" in {
    trait Test {
      def one: Byte
      def two(x: Byte): Byte
      def three(x: Byte, y: Byte): Byte
    }
    val factory = RootClass.newClass[Test]("""
class Test {
public:
  char one() { return 10; }
  char two(char x) { return -x; }
  char three(char x, char y) { return x + y; }
};
""")
    val instance = factory.newInstance
    instance.one should be (10)
    instance.two(10) should be (-10)
    instance.three(10, 20) should be (30)
    instance.three(126, 1) should be (127)
    instance.three(127, 1) should be (-128)
  }

  it must "compute short expressions" in {
    trait Test {
      def one: Short
      def two(x: Short): Short
      def three(x: Short, y: Short): Short
    }
    val factory = RootClass.newClass[Test]("""
class Test {
public:
  short one() { return 10; }
  short two(short x) { return -x; }
  short three(short x, short y) { return x + y; }
};
""")
    val instance = factory.newInstance
    instance.one should be (10)
    instance.two(10) should be (-10)
    instance.three(10, 20) should be (30)
    instance.three(32766, 1) should be (32767)
    instance.three(32767, 1) should be (-32768)
  }

  it must "compute int expressions" in {
    trait Test {
      def one: Int
      def two(x: Int): Int
      def three(x: Int, y: Int): Int
    }
    val factory = RootClass.newClass[Test]("""
class Test {
public:
  int one() { return 10; }
  int two(int x) { return -x; }
  int three(int x, int y) { return x + y; }
};
""")
    val instance = factory.newInstance
    instance.one should be (10)
    instance.two(10) should be (-10)
    instance.three(10, 20) should be (30)
    instance.three(2147483646, 1) should be (2147483647)
    instance.three(2147483647, 1) should be (-2147483648)
  }

  it must "compute long expressions" in {
    trait Test {
      def one: Long
      def two(x: Long): Long
      def three(x: Long, y: Long): Long
    }
    val factory = RootClass.newClass[Test]("""
class Test {
public:
  long one() { return 10; }
  long two(long x) { return -x; }
  long three(long x, long y) { return x + y; }
};
""")
    val instance = factory.newInstance
    instance.one should be (10)
    instance.two(10) should be (-10)
    instance.three(10, 20) should be (30)
    instance.three(9223372036854775806L, 1) should be (9223372036854775807L)
    instance.three(9223372036854775807L, 1) should be (-9223372036854775808L)
  }

  it must "compute float expressions" in {
    trait Test {
      def one: Float
      def two(x: Float): Float
      def three(x: Float, y: Float): Float
    }
    val factory = RootClass.newClass[Test]("""
class Test {
public:
  float one() { return 3.14; }
  float two(float x) { return -x; }
  float three(float x, float y) { return x + y; }
};
""")
    val instance = factory.newInstance
    instance.one should be (3.14F +- 1e-6F)
    instance.two(3.14F) should be (-3.14F +- 1e-6F)
    instance.three(3.14F, 0.1F) should be (3.24F +- 1e-6F)
  }

  it must "compute double expressions" in {
    trait Test {
      def one: Double
      def two(x: Double): Double
      def three(x: Double, y: Double): Double
    }
    val factory = RootClass.newClass[Test]("""
class Test {
public:
  double one() { return 3.14; }
  double two(double x) { return -x; }
  double three(double x, double y) { return x + y; }
};
""")
    val instance = factory.newInstance
    instance.one should be (3.14 +- 1e-6)
    instance.two(3.14) should be (-3.14 +- 1e-6)
    instance.three(3.14, 0.1) should be (3.24 +- 1e-6)
  }

  it must "compute string expressions" in {
    trait Test {
      def one: String
      def two(x: String): String
      def three(x: String, y: String): String
    }
    val factory = RootClass.newClass[Test]("""
class Test {
public:
  const char *one() { return "hello"; }
  const char *two(const char *x) { return x; }
  const char *three(const char *x, const char *y) { return x; }
};
""")
    val instance = factory.newInstance
    instance.one should be ("hello")
    instance.two("hey") should be ("hey")
    instance.three("HEY", "THERE") should be ("HEY")
  }

  it must "pass opaque pointers" in {
    trait Test {
      def fill(x: Double): Int
      def binContent(bin: Int): Double

      def one: Pointer
      def two(x: Pointer): Pointer
      def three(x: Pointer, y: Pointer): Pointer
    }

    val factory = RootClass.newClass[Test]("""
static int counter = 0;

class Test {
private:
  std::string name;
  TH1F *hist;
public:
  Test() {
    name = std::string("hist") + std::to_string(counter);
    counter++;
    hist = new TH1F(name.c_str(), "", 100, 0, 1);
  }
  int    fill(double x) { return hist->Fill(x); }
  double binContent(int bin) { return hist->GetBinContent(bin); }

  TH1F *one() { return hist; }
  TH1F *two(TH1F *x) { return x; }
  TH1F *three(TH1F *x, TH1F *y) { x->Add(y); return x; }
};
""")

    val instance1 = factory.newInstance
    instance1.fill(0.5) should be (51)  // area 51
    instance1.binContent(51) should be (1.0 +- 1e-6)

    val instance2 = factory.newInstance
    instance2.fill(0.5) should be (51)

    val instance1_hist = instance1.one
    val instance2_hist = instance2.one

    instance1_hist should not be (instance2_hist)
    instance1_hist should be (instance1.one)

    instance1.two(instance1_hist) should be (instance1_hist)

    instance1.three(instance1.one, instance2.one)
    instance1.binContent(51) should be (2.0 +- 1e-6)
  }

  it must "permit functions that return nothing" in {
    trait Test {
      def fill(x: Double)
      def binContent(bin: Int): Double
    }
    val factory = RootClass.newClass[Test]("""
class Test {
private:
  TH1F *hist;
public:
  Test() {
    hist = new TH1F("hist2", "", 100, 0, 1);
  }
  void   fill(double x) { hist->Fill(x); }
  double binContent(int bin) { return hist->GetBinContent(bin); }
};
""")
    val instance = factory.newInstance
    instance.fill(0.5)
    instance.binContent(51) should be (1.0 +- 1e-6)
  }

  "RootInstance objects" must "be inspectable" in {
  }

  "RootClass objects" must "be serializable" in {
    trait Test {
      def get: Int
      def put(x: Int)
    }

    val originalClass = RootClass.newClass[Test]("""
class Test {
private:
  int value;
public:
  Test() { value = 999; }
  int get() { return value; }
  void put(int x) { value = x; }
};
""")

    val instance1 = originalClass.newInstance
    instance1.get should be (999)
    instance1.put(5)
    instance1.get should be (5)

    val baos = new java.io.ByteArrayOutputStream
    val outputStream = new java.io.ObjectOutputStream(baos)
    outputStream.writeObject(originalClass)

    val bais = new java.io.ByteArrayInputStream(baos.toByteArray)
    val inputStream = new java.io.ObjectInputStream(bais)
    val clonedClass = inputStream.readObject.asInstanceOf[RootClass[Test]]

    val instance2 = clonedClass.newInstance
    instance2.get should be (999)
    instance2.put(12)
    instance2.get should be (12)
  }
}
