import sbtrelease._
import ReleaseStateTransformations._
import sbtrelease.ReleaseStateTransformations._
import sbtversionpolicy.withsbtrelease.ReleaseVersion

val scroogeVersion = "22.1.0"
val thriftVersion = "0.20.0"

val artifactProductionSettings = Seq(
  organization := "com.gu",
  scalaVersion := "2.13.17",
  // scrooge 21.3.0: Builds are now only supported for Scala 2.12+
  // https://twitter.github.io/scrooge/changelog.html#id11
  crossScalaVersions := Seq("2.12.18", scalaVersion.value),
  scalacOptions ++= Seq("-release:11"),// going ahead with release option only. We might add more options if any implementation comes in future :  ("-feature", "-deprecation", "-unchecked", "-Xfatal-warnings")
  licenses := Seq(License.Apache2),
  Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-u", s"test-results/scala-${scalaVersion.value}", "-o")
)

lazy val root = (project in file("."))
  .settings(artifactProductionSettings)
  .aggregate(thrift, scalaClasses)
  .settings(
    publish / skip := true,
    releaseVersion := ReleaseVersion.fromAggregatedAssessedCompatibilityWithLatestRelease().value,
    releaseCrossBuild := true,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      setNextVersion,
      commitNextVersion
    )
  )

lazy val scalaClasses = (project in file("scala"))
  .settings(artifactProductionSettings)
  .settings(
    name := "content-entity-model",
    description := "Scala library built from Content-entity thrift definition",
    Compile / scroogeThriftSourceFolder := baseDirectory.value / "../thrift/src/main/thrift",
    Compile / scroogeThriftOutputFolder := sourceManaged.value,
    Compile / scroogePublishThrift := true,
    Compile / managedSourceDirectories += (Compile / scroogeThriftOutputFolder).value,

    libraryDependencies ++= Seq(
      "org.apache.thrift" % "libthrift" % thriftVersion,
      "com.twitter" %% "scrooge-core" % scroogeVersion,
      "org.scalacheck" %% "scalacheck" % "1.17.0" % "test"
    )
  )

lazy val thrift = (project in file("thrift"))
  .settings(artifactProductionSettings)
  .disablePlugins(ScroogeSBT)
  .settings(
    name := "content-entity-thrift",
    description := "Content entity model Thrift files",
    crossPaths := false,
    packageDoc / publishArtifact := false,
    packageSrc / publishArtifact := false,
    Compile / unmanagedResourceDirectories += { baseDirectory.value / "src/main/thrift" }
  )

lazy val typescriptClasses = (project in file("ts"))
  .enablePlugins(ScroogeTypescriptGen)
  .settings(artifactProductionSettings)
  .settings(
    publish / skip := true,
    name := "content-entity-typescript",
    scroogeTypescriptNpmPackageName := "@guardian/content-entity-model",
    Compile / scroogeDefaultJavaNamespace := scroogeTypescriptNpmPackageName.value,
    Test / scroogeDefaultJavaNamespace := scroogeTypescriptNpmPackageName.value,
    description := "Typescript library built from Content-entity thrift definition",
    Compile / scroogeLanguages := Seq("typescript"),
    Compile / scroogeThriftSourceFolder := baseDirectory.value / "../thrift/src/main/thrift",
    scroogeTypescriptPackageLicense := "Apache-2.0"
  )