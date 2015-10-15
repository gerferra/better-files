lazy val commonSettings = Seq(
  version := "2.13.0",
  organization := "com.github.pathikrit",
  scalaVersion := "2.11.7",
  crossScalaVersions := Seq("2.10.5", "2.11.7"),
  crossVersion := CrossVersion.binary,
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint"),
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:implicitConversions",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xlint",
    "-Yinline-warnings",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    //"-Ywarn-numeric-widen",     // issue in 2.10
    //"-Ywarn-value-discard",
    //"-Ywarn-unused-import",     // 2.11 only
    "-Xfuture"
  ),
  libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.5" % Test
)

lazy val core = (project in file("core"))
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "better-files",
    description := "Simple, safe and intuitive I/O in Scala"
  )

lazy val akka = (project in file("akka"))
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    name := "better-files-akka",
    description := "Reactive file watchers",
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.14"
  )
  .dependsOn(core)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(scoverageSettings: _*)
  .settings(docSettings: _*)
  .aggregate(core, akka)

lazy val docSettings = unidocSettings ++ site.settings ++ ghpages.settings ++ Seq(
  autoAPIMappings := true,
  SiteKeys.siteSourceDirectory := file("site"),
  site.addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), "latest/api"),
  git.remoteRepo := "git@github.com:pathikrit/better-files.git"
)

lazy val publishSettings = Seq(
  homepage := Some(url("https://github.com/pathikrit/better-files")),
  licenses += "MIT" -> url("http://opensource.org/licenses/MIT"),
  scmInfo := Some(ScmInfo(url("https://github.com/pathikrit/better-files"), "git@github.com:pathikrit/better-files.git")),
  apiURL := Some(url("https://pathikrit.github.io/better-files/latest/api/")),
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomExtra :=
    <developers>
      <developer>
        <id>pathikrit</id>
        <name>Pathikrit Bhowmick</name>
        <url>http://github.com/pathikrit</url>
      </developer>
    </developers>
  ,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("Snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("Releases" at nexus + "service/local/staging/deploy/maven2")
  },
  credentials ++= (for {
    username <- sys.props.get("SONATYPE_USERNAME")
    password <- sys.props.get("SONATYPE_PASSWORD")
  } yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq
)

import ScoverageSbtPlugin._
lazy val scoverageSettings = Seq(
  ScoverageKeys.coverageMinimum := 75,
  ScoverageKeys.coverageFailOnMinimum := true
)
