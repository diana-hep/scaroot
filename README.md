# ScaROOT
Call ROOT or arbitrary C++ code from Scala

## Motivation

ROOT is a popular framework for high energy physics data. Most "big data" frameworks, such as Hadoop and Spark, are implemented in Java or Scala. ScaROOT allows you to call ROOT functions from Scala so that ROOT can be used to perform calculations in a big data workflow.

(This is for accessing ROOT library functions. See `root2avro` for a high-throughput data feed.)

## Examples

ScaROOT works by linking Scala traits (abstract interfaces) to C++ code that may contain ROOT calls.

```scala
import org.dianahep.scaroot.RootClass

// Scala trait (abstract interface):
trait ChiSqProb {
  def apply(chi2: Double, ndof: Int): Double
}

// C++ class definition that satisfies the interface:
val chiSqProbClass = RootClass[ChiSqProb]("""
class ChiSqProb {
public:
  double apply(double chi2, int ndof) {
    return ROOT::Math::chisquared_cdf(chi2, ndof);
  }
};
""")

// Create an instance:
val chiSqProb = chiSqProbClass.newInstance

// And use it:
chiSqProb.apply(53.8, 50)
0.6689797343068249
```

Or better yet, call the instance like a function. (In Scala, the `apply` method is the equivalent of C++'s `operator()`.)

```scala
chiSqProb(53.8, 50)
0.6689797343068249
```

The class instance can maintain state and some functions can be defined in Scala.

```scala
trait Histogram {
  var name: String = ""
  var bins: Int = 0
  var low: Double = 0
  var high: Double = 0
  def init(name: String, bins: Int, low: Double, high: Double) {
    this.name = name
    this.bins = bins
    this.low = low
    this.high = high
    initroot(name, bins, low, high)
  }
  def initroot(name: String, bins: Int, low: Double, high: Double)
  def fill(x: Double)
  def get(bin: Int): Double
  def getall: Array[Double] = 1 to bins map {i => get(i)} toArray
}

val histogramClass = RootClass[Histogram]("""
class Histogram {
private:
  TH1D *hist;
public:
  void initroot(const char *name, int bins, double low, double high) {
    hist = new TH1D(name, "", bins, low, high);
  }
  void fill(double x) {
    if (hist != nullptr) hist->Fill(x);
  }
  double get(int bin) {
    if (hist != nullptr)
      return hist->GetBinContent(bin);
    else
      return 0.0;
  }
};
""")

val hist1 = histogramClass.newInstance
hist1.init("myhist", 10, 0, 1)

0 until 10000 foreach {i => hist1.fill(scala.util.Random.nextDouble()) }

hist1.getall
Array(950.0, 996.0, 960.0, 1001.0, 1010.0, 982.0, 1067.0, 956.0, 1049.0, 1029.0)
```






---

to write Scala code that calls ROOT through its ability to link C++ class definitions to Scala traits.

Once defined, calls from Scala to ROOT code are direct: no secondary process is managed and no data are serialized; they are only copied from the JVM's garbage collected heap to the C++ memory space and back (with a big-endian/little-endian swap, if necessary).

ScaROOT is similar to PyROOT, which provides access to ROOT from Python. However, ScaROOT requires an interface to be explicitly defined because class field access in the JVM is not as dynamic as it is in Python (and presumably should be faster, too).

ScaROOT uses Scala macros to generate the instrumented JVM classes at compile-time and ROOT's interpreter (CINT or Cling) to define and connect C++ classes at run-time. For Scala, "compile-time" may be on the Scala or Spark prompt.

## Example



## Dependencies



## Limitations

  * Scala traits cannot have constructors, and so the C++ function must have a zero-argument constructor (implicitly or explicitly).
  * Class methods declared in Scala and defined in C++ can only have primitives for arguments and return values: `Boolean` (`bool` in C++), `Byte` (`char` in C++), `Short`, `Int`, `Long`, `Float`, `Double`, `String` (`char*` in C++), or an opaque `com.sun.jna.Pointer` to C++ data.

