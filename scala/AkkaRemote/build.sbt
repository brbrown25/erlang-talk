name := "AkkaRemote"

version := "1.0"

scalaVersion := "2.12.6"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.14",
  "com.typesafe.akka" %% "akka-remote" % "2.5.14",
  "com.typesafe" % "config" % "1.3.3",
  "org.scalaz" %% "scalaz-core" % "7.2.21"
)
