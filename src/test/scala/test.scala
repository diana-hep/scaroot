package test.scala.scaroot

import scala.collection.mutable
import scala.language.postfixOps

import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

import com.sun.jna.Pointer
import com.sun.jna.ptr.PointerByReference
import com.sun.jna.Memory
import com.sun.jna.Native

import org.dianahep.scaroot._

class DefaultSuite extends FlatSpec with Matchers {
  // Test classes must have unique names because they share a
  // namespace in C++ (TInterpreter fills only one C++ "ClassLoader").
  // Therefore, we number the tests like lines in BASIC: starting at
  // 100, increasing in increments of 10, so that new tests can be
  // inserted without disrupting the order.  :)

  "ROOT in Scala" must "compute boolean expressions" in {
    trait Test100 {
      def one: Boolean
      def two(x: Boolean): Boolean
      def three(x: Boolean, y: Boolean): Boolean
    }
    val factory = RootClassFactory.newClass[Test100]("""
class Test100 {
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
    trait Test110 {
      def one: Byte
      def two(x: Byte): Byte
      def three(x: Byte, y: Byte): Byte
    }
    val factory = RootClassFactory.newClass[Test110]("""
class Test110 {
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
    trait Test120 {
      def one: Short
      def two(x: Short): Short
      def three(x: Short, y: Short): Short
    }
    val factory = RootClassFactory.newClass[Test120]("""
class Test120 {
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
    trait Test130 {
      def one: Int
      def two(x: Int): Int
      def three(x: Int, y: Int): Int
    }
    val factory = RootClassFactory.newClass[Test130]("""
class Test130 {
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
    trait Test140 {
      def one: Long
      def two(x: Long): Long
      def three(x: Long, y: Long): Long
    }
    val factory = RootClassFactory.newClass[Test140]("""
class Test140 {
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
    trait Test150 {
      def one: Float
      def two(x: Float): Float
      def three(x: Float, y: Float): Float
    }
    val factory = RootClassFactory.newClass[Test150]("""
class Test150 {
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
    trait Test160 {
      def one: Double
      def two(x: Double): Double
      def three(x: Double, y: Double): Double
    }
    val factory = RootClassFactory.newClass[Test160]("""
class Test160 {
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
    trait Test170 {
      def one: String
      def two(x: String): String
      def three(x: String, y: String): String
    }
    val factory = RootClassFactory.newClass[Test170]("""
class Test170 {
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



}
