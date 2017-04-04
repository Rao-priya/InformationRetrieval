name := """helloworldplease"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.elasticsearch" %"elasticsearch" % "2.3.2",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.7.4"
  )


fork in run := true