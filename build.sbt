name := "sssh"

description := "A Simple scala ssh library"

scalaVersion := "2.11.8"

organization := "com.github.mideo"

lazy val `sssh` = (project in file("."))
  .settings(
    scalacOptions := Seq(
      "-unchecked",
      "-deprecation",
      "-encoding",
      "utf8",
      "-feature",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-language:reflectiveCalls",
      "-Yrangepos"
    )
  )

fork in run := true

resolvers ++= Seq(
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  "Sonatypes" at "https://oss.sonatype.org/content/repositories/releases",
  "Maven Repo" at "http://mvnrepository.com/maven2/"
)

libraryDependencies ++= Seq(
  "com.jcraft" % "jsch" % "0.1.54",
  "com.typesafe" % "config" % "1.2.1",
  "org.scalatest" % "scalatest_2.11" % "3.0.3" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)

pomIncludeRepository := { _ => false }

publishMavenStyle := true

publishArtifact in Test := false

val oss_user = if (sys.env.keySet.contains("OSS_USERNAME")) sys.env("OSS_USERNAME") else ""
val oss_pass = if (sys.env.keySet.contains("OSS_PASSWORD")) sys.env("OSS_PASSWORD") else ""
val gpg_pass = if (sys.env.keySet.contains("GPG_PASSWORD")) sys.env("GPG_PASSWORD").toCharArray else Array.emptyCharArray

credentials += Credentials(
  "Sonatype Nexus Repository Manager",
  "oss.sonatype.org", oss_user, oss_pass)

pgpPassphrase := Some(gpg_pass)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php"))

homepage := Some(url("https://github.com/MideO/sssh"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/MideO/sssh"),
    "scm:git@github.com/MideO/sssh"
  )
)

developers := List(
  Developer(
    id = "mideo",
    name = "Mide Ojikutu",
    email = "mide.ojikutu@gmail.com",
    url = url("https://github.com/MideO")
  )
)

val tagName = Def.setting {
  s"v${if (releaseUseGlobalVersion.value) (version in ThisBuild).value else version.value}"
}
val tagOrHash = Def.setting {
  if (isSnapshot.value)
    sys.process.Process("git rev-parse HEAD").lines_!.head
  else
    tagName.value
}


// Release
import ReleaseTransformations._

releaseVersionBump := sbtrelease.Version.Bump.Next

releaseIgnoreUntrackedFiles := true

releasePublishArtifactsAction := PgpKeys.publishSigned.value

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
  pushChanges
)