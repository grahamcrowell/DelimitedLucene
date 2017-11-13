name := "SourceSearch"

version := "0.1"

scalaVersion := "2.11.8"

// https://github.com/pathikrit/better-files
libraryDependencies += "com.github.pathikrit" %% "better-files" % "2.17.1"
// https://github.com/nscala-time/nscala-time
libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "1.8.0"

// scala test
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

// apache logging
libraryDependencies ++= Seq(
  "org.apache.logging.log4j" % "log4j-api" % "2.9.1",
  "org.apache.logging.log4j" % "log4j-core" % "2.9.1",
  //https://github.com/apache/logging-log4j-scala
  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0"
)

// apache lucene
libraryDependencies ++= Seq(
  "org.apache.lucene" % "lucene-core" % "7.1.0",
  "org.apache.lucene" % "lucene-analyzers-common" % "7.1.0",
  "org.apache.lucene" % "lucene-analyzers-icu" % "7.1.0",
  "org.apache.lucene" % "lucene-queryparser" % "7.1.0"
)
// https://mvnrepository.com/artifact/com.google.guava/guava
libraryDependencies += "com.google.guava" % "guava" % "23.3-jre"