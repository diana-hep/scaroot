scalaVersion := "2.11.7"

resolvers += "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.7"
libraryDependencies += "org.freehep" % "freehep-rootio" % "2.7-SNAPSHOT"
libraryDependencies += "net.java.dev.jna" % "jna" % "4.2.1"

initialCommands in console += """
import org.dianahep.scaroot._

case class TwoMuon(mass_mumu: Float, px: Float, py: Float, pz: Float) {
  def momentum = Math.sqrt(px*px + py*py + pz*pz)
  def energy = Math.sqrt(mass_mumu*mass_mumu + px*px + py*py + pz*pz)
}

"""
