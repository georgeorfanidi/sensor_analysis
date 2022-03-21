ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file(".")).aggregate(sensorsAnalysis, bigFileGen)

lazy val sensorsAnalysis = (project in file("sensorsAnalysis"))
  .settings(
    name := "sensors_analysis",
    libraryDependencies ++= Seq(
      "com.github.tototoshi" %% "scala-csv" % "1.3.10",
      "org.typelevel" %% "cats-effect" % "3.3.8",
      "org.typelevel" %% "cats-effect-testing-scalatest" % "1.4.0" % Test
    )
  )

lazy val bigFileGen = project in file("bigFileGen")
