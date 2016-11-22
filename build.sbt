import com.github.bigtoast.sbtthrift.ThriftPlugin._
import com.twitter.scrooge.ScroogeSBT

import sbtrelease._

import ReleaseStateTransformations._

Sonatype.sonatypeSettings

val commonSettings = Seq(
  organization := "com.gu",
  scalaVersion := "2.11.8",
  scmInfo := Some(ScmInfo(url("https://github.com/guardian/content-entity"),
                          "scm:git:git@github.com:guardian/content-entity.git")),

  pomExtra := (
  <url>https://github.com/guardian/content-entity</url>
  <developers>
    <developer>
      <id>LATaylor-guardian</id>
      <name>Luke Taylor</name>
      <url>https://github.com/LATaylor-guardian</url>
    </developer>
    <developer>
      <id>tomrf1</id>
      <name>Tom Forbes</name>
      <url>https://github.com/tomrf1</url>
    </developer>
  </developers>
  ),
  licenses := Seq("Apache V2" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),

  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    releaseStepCommand("sonatypeReleaseAll"),
    pushChanges
  )
)

lazy val root = (project in file("."))
  .aggregate(thrift, scala)
  .settings(commonSettings)
  .settings(
    publishArtifact := false
  )

lazy val scala = (project in file("scala"))
  .settings(ScroogeSBT.newSettings: _*)
  .settings(commonSettings)
  .settings(
    name := "content-entity-model",
    description := "Scala library built from Content-entity thrift definition",

    scroogeThriftSourceFolder in Compile := baseDirectory.value / "../thrift/src/main/thrift",
    includeFilter in unmanagedResources := "*.thrift",
    unmanagedResourceDirectories in Compile += baseDirectory.value / "../thrift/src/main/thrift",
    managedSourceDirectories in Compile += (scroogeThriftOutputFolder in Compile).value,
    libraryDependencies ++= Seq(
      "org.apache.thrift" % "libthrift" % "0.9.2",
      "com.twitter" %% "scrooge-core" % "3.17.0"
    )
  )

lazy val thrift = (project in file("thrift"))
  .settings(commonSettings)
  .settings(
    name := "content-entity-thrift",
    description := "Content entity model Thrift files",
    crossPaths := false,
    publishArtifact in packageDoc := false,
    publishArtifact in packageSrc := false,
    unmanagedResourceDirectories in Compile += { baseDirectory.value / "src/main/thrift" }
  )

// settings for the thrift plugin, both default and custom
thriftSettings ++ inConfig(Thrift) {

  // add the node option to the js generator, as that is the style of
  // code that we want to generate

  Seq(
    thriftSourceDir := file("thrift/src/main/thrift"),
    thriftJsEnabled := true,
    thriftJavaEnabled := false,
    thriftJsOptions := Seq("node"),
    thriftOutputDir <<= baseDirectory / "generated",
    thriftJsOutputDir <<= thriftOutputDir
  )
}

