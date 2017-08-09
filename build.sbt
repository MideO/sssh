name := "sssh"

description := "A Simple scala ssh library"

scalaVersion := "2.11.8"

version := "0.0.1-SNAPSHOT"

organization := "com.github.mideo"

useGpg := true

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

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php"))

homepage := Some(url("https://github.com/MideO/sssh"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/MideO/scala-jsch"),
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