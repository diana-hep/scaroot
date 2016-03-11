# scaroot
Call ROOT or arbitrary C++ code from Scala

## Motivation

ROOT is a popular framework for high energy physics data. Most "big data" frameworks, such as Hadoop and Spark, are implemented in Java or Scala. ScaROOT allows you to call ROOT functions from Scala so that ROOT can be used to perform calculations in a big data workflow.

## Examples

Access a statistical function:

```scala
import org.dianahep.scaroot.RootClass

trait ChiSqProb {
  def calculate(chi2: Double, ndof: Int): Double
}

val chiSqProbClass = RootClass[ChiSqProb]("""
class ChiSqProb {
public:
  double calculate(double chi2, int ndof) {
    return ROOT::Math::chisquared_cdf(chi2, ndof);
  }
};
""")

val chiSqInstance = chiSqProbClass.newInstance

println(chiSqInstance.calculate(53.8, 50))
0.6689797343068249
```






to write Scala code that calls ROOT through its ability to link C++ class definitions to Scala traits.

Once defined, calls from Scala to ROOT code are direct: no secondary process is managed and no data are serialized; they are only copied from the JVM's garbage collected heap to the C++ memory space and back (with a big-endian/little-endian swap, if necessary).

ScaROOT is similar to PyROOT, which provides access to ROOT from Python. However, ScaROOT requires an interface to be explicitly defined because class field access in the JVM is not as dynamic as it is in Python (and presumably should be faster, too).

ScaROOT uses Scala macros to generate the instrumented JVM classes at compile-time and ROOT's interpreter (CINT or Cling) to define and connect C++ classes at run-time. For Scala, "compile-time" may be on the Scala or Spark prompt.

## Example



## Dependencies



## Limitations

  * Scala traits cannot have constructors, and so the C++ function must have a zero-argument constructor (implicitly or explicitly).
  * Class methods declared in Scala and defined in C++ can only have primitives for arguments and return values: `Boolean` (`bool` in C++), `Byte` (`char` in C++), `Short`, `Int`, `Long`, `Float`, `Double`, `String` (`char*` in C++), or an opaque `com.sun.jna.Pointer` to C++ data.

