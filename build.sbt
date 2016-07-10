name := "usher"
version := "0.2.0-SNAPSHOT"
organization := "com.markfeeney"
scalaVersion := "2.11.8"
scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings",
  "-Xlint",
  "-Ywarn-unused-import"
)
libraryDependencies ++= Seq(
  "com.markfeeney" % "circlet_2.11" % "0.1.0",
  "org.scalatest" % "scalatest_2.11" % "2.2.6" % "test"
)

// Stuff related to publishing to sonatype
// Reference: http://www.loftinspace.com.au/blog/publishing-scala-libraries-to-sonatype.html
publishMavenStyle := true
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
publishArtifact in Test := false
pomIncludeRepository := { _ => false }
licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))
homepage := Some(url("https://github.com/overthink/usher"))
pomExtra := (
  <scm>
    <url>git@github.com:overthink/usher.git</url>
    <connection>scm:git:git@github.com:overthink/usher.git</connection>
  </scm>
  <developers>
    <developer>
      <id>overthink</id>
      <name>Mark Feeney</name>
      <url>http://proofbyexample.com</url>
    </developer>
  </developers>)

antlr4Settings
antlr4PackageName in Antlr4 := Some("com.markfeeney.usher.parser")

