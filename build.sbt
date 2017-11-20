name := "moving-crypto"

version := "1.0"

scalaVersion := "2.12.2"

lazy val akkaVersion = "2.5.6"
lazy val akkaHttpVersion   = "10.0.10"

libraryDependencies ++= Seq(
  "com.google.inject" % "guice" % "4.1.0",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %%  "akka-slf4j"      % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "com.typesafe" % "config" % "1.3.2",
  "joda-time" % "joda-time" % "2.9.9",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.play" %% "play-json" % "2.6.7",
  "org.mockito" % "mockito-core" % "2.12.0" % "test"

)
