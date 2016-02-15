scalaVersion := "2.11.7"

resolvers += "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository"
resolvers += "cloudera" at "https://repository.cloudera.com/artifactory/cloudera-repos/"

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.7"
libraryDependencies += "org.freehep" % "freehep-rootio" % "2.7-SNAPSHOT"
libraryDependencies += "net.java.dev.jna" % "jna" % "4.2.1"

libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.6.0-cdh5.5.1"
libraryDependencies += "org.apache.hadoop" % "hadoop-core" % "2.6.0-cdh5.5.1"

initialCommands in console += """
import org.dianahep.scaroot._
"""
