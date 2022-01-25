import sbtrelease._
import ReleaseStateTransformations._

val scroogeVersion = "22.1.0"
val thriftVersion = "0.15.0"
val betaReleaseType = "beta"
val betaReleaseSuffix = "-beta.0"

lazy val versionSettingsMaybe = {
  sys.props.get("RELEASE_TYPE").map {
    case v if v == betaReleaseType => betaReleaseSuffix
  }.map { suffix =>
    releaseVersion := {
      ver => Version(ver).map(_.withoutQualifier.string).map(_.concat(suffix)).getOrElse(versionFormatError(ver))
    }
  }.toSeq
}

lazy val mavenSettings = Seq(
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
        <developer>
          <id>justinpinner</id>
          <name>Justin Pinner</name>
          <url>https://github.com/justinpinner</url>
        </developer>
      </developers>
    ),
  licenses := Seq("Apache V2" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),
  publishTo := sonatypePublishToBundle.value,
  publishConfiguration := publishConfiguration.value.withOverwrite(true)
)

lazy val checkReleaseType: ReleaseStep = ReleaseStep({ st: State =>
  val releaseType = sys.props.get("RELEASE_TYPE").map {
    case v if v == betaReleaseType => betaReleaseType.toUpperCase
  }.getOrElse("PRODUCTION")

  SimpleReader.readLine(s"This will be a $releaseType release. Continue? [y/N]: ") match {
    case Some(v) if Seq("Y", "YES").contains(v.toUpperCase) => // we don't care about the value - it's a flow control mechanism
    case _ => sys.error(s"Release aborted by user!")
  }
  // we haven't changed state, just pass it on if we haven't thrown an error from above
  st
})

lazy val releaseProcessSteps: Seq[ReleaseStep] = {
  val commonSteps = Seq(
    checkReleaseType,
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest
  )

  val prodSteps: Seq[ReleaseStep] = Seq(
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    releaseStepCommandAndRemaining("+publishSigned"),
    releaseStepCommand("sonatypeBundleRelease"),
    setNextVersion,
    commitNextVersion
  )

  /*
  Beta assemblies can be published to Sonatype and Maven.

  To make this work, start SBT with the candidate RELEASE_TYPE variable set;
    sbt -DRELEASE_TYPE=beta

  This gets around the "problem" of sbt-sonatype assuming that a -SNAPSHOT build should not be delivered to Maven.

  In this mode, the version number will be presented as e.g. 1.2.3.beta.n, but the git tagging and version-updating
  steps are not triggered, so it's up to the developer to keep track of what was released and manipulate subsequent
  release and next versions appropriately.
  */
  val betaSteps: Seq[ReleaseStep] = Seq(
    setReleaseVersion,
    releaseStepCommandAndRemaining("+publishSigned"),
    releaseStepCommand("sonatypeBundleRelease"),
    setNextVersion
  )

  commonSteps ++ (sys.props.get("RELEASE_TYPE") match {
    case Some(v) if v == betaReleaseType => betaSteps // this enables a beta build to sonatype and Maven
    case None => prodSteps  // our normal deploy route
  })

}

val commonSettings = Seq(
  organization := "com.gu",
  scalaVersion := "2.13.2",
  // scrooge 21.3.0: Builds are now only supported for Scala 2.12+
  // https://twitter.github.io/scrooge/changelog.html#id11
  crossScalaVersions := Seq("2.12.11", scalaVersion.value),
  releaseCrossBuild := true,
  scmInfo := Some(ScmInfo(url("https://github.com/guardian/content-entity"),
                          "scm:git:git@github.com:guardian/content-entity.git")),
  releasePublishArtifactsAction := PgpKeys.publishSigned.value
) ++ mavenSettings ++ versionSettingsMaybe

lazy val root = (project in file("."))
  .settings(commonSettings)
  .aggregate(thrift, scalaClasses)
  .settings(
    publishArtifact := false,
    releaseProcess := releaseProcessSteps
  )

lazy val scalaClasses = (project in file("scala"))
  .settings(commonSettings)
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
      "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"
    )
  )

lazy val thrift = (project in file("thrift"))
  .settings(commonSettings)
  .disablePlugins(ScroogeSBT)
  .settings(
    name := "content-entity-thrift",
    description := "Content entity model Thrift files",
    crossPaths := false,
    packageDoc / publishArtifact := false,
    packageSrc / publishArtifact := false,
    Compile / unmanagedResourceDirectories += { baseDirectory.value / "src/main/thrift" }
  )

lazy val npmBetaReleaseTagMaybe =
  sys.props.get("RELEASE_TYPE").map {
    case v if v == betaReleaseType =>
      // Why hard-code "beta" instead of using the value of the variable? That's to ensure it's always presented as
      // --tag beta to the npm release process provided by the ScroogeTypescriptGen plugin regardless of how we identify
      // a beta release here
      scroogeTypescriptPublishTag := "beta"
  }.toSeq

lazy val typescriptClasses = (project in file("ts"))
  .enablePlugins(ScroogeTypescriptGen)
  .settings(commonSettings)
  .settings(npmBetaReleaseTagMaybe)
  .settings(
    publishArtifact := false,
    name := "content-entity-typescript",
    scroogeTypescriptNpmPackageName := "@guardian/content-entity-model",
    Compile / scroogeDefaultJavaNamespace := scroogeTypescriptNpmPackageName.value,
    Test / scroogeDefaultJavaNamespace := scroogeTypescriptNpmPackageName.value,
    description := "Typescript library built from Content-entity thrift definition",
    Compile / scroogeLanguages := Seq("typescript"),
    Compile / scroogeThriftSourceFolder := baseDirectory.value / "../thrift/src/main/thrift",
    scroogeTypescriptPackageLicense := "Apache-2.0"
  )