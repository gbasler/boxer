import sbt._
import Keys._

object build extends Build {
  lazy val root = Project(
    id = "root",
    base = file("."),
    aggregate = Seq(plugin, main, library),
    settings = sharedSettings
  )

  lazy val sharedSettings = Defaults.defaultSettings ++ Seq(
    scalaVersion := "2.11.4",
    organization := "demo"
//    name := "boxer"
  )

  // This subproject contains a Scala compiler plugin
  lazy val plugin = Project(
    id = "plugin",
    base = file("plugin"),
    settings = sharedSettings ++ Seq[Project.Setting[_]](
      libraryDependencies += ("org.scala-lang" % "scala-compiler" % scalaVersion.value),
      publishArtifact in Compile := false
    ),
    dependencies = Seq[ClasspathDep[ProjectReference]](library)
  )

  // Scalac command line options to install our compiler plugin.
  lazy val usePluginSettings = Seq(
    scalacOptions in Compile ++= {
      val jar: File = (Keys.`package` in(plugin, Compile)).value
      val addPlugin = "-Xplugin:" + jar.getAbsolutePath
      // add plugin timestamp to compiler options to trigger recompile of
      // main after editing the plugin. (Otherwise a 'clean' is needed.)
      val dummy = "-Jdummy=" + jar.lastModified
      Seq(addPlugin, dummy)
    }
  )

  // This subproject contains the linked set code
  lazy val library = Project(
    id = "library",
    base = file("library"),
    settings = sharedSettings //++ Seq[Project.Setting[_]](
//      libraryDependencies += ("org.scala-lang" % "scala-compiler" % scalaVersion.value)
//    )
  )


  // A regular module with the application code.
  lazy val main = Project(
    id = "main",
    base = file("main"),
    settings = sharedSettings ++ usePluginSettings,
    dependencies = Seq[ClasspathDep[ProjectReference]](library)
  )
}
