addSbtPlugin("com.gu" % "sbt-scrooge-typescript" % "4.0.2")

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.4.0")

addSbtPlugin("ch.epfl.scala" % "sbt-version-policy" % "3.2.1")

// Patch the build tool graph without changing published library dependencies.
dependencyOverrides += "org.codehaus.plexus" % "plexus-utils" % "3.6.1"
