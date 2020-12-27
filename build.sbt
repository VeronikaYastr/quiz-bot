
name := "quiz-bot"

version := "0.1"

scalaVersion := "2.13.3"

val http4sVersion = "0.21.8"
val circeVersion = "0.13.0"
val doobieVersion = "0.8.8"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-Ymacro-annotations",
  "-Xfatal-warnings",
  "-unchecked",
)

libraryDependencies ++= Seq(
  "org.typelevel"         %% "cats-core"            % "2.1.0",
  "org.flywaydb"           % "flyway-core"          % "5.2.4",
  "org.tpolecat"          %% "doobie-core"          % doobieVersion,
  "org.tpolecat"          %% "doobie-postgres"      % doobieVersion,
  "org.tpolecat"          %% "doobie-hikari"        % doobieVersion,
  "org.tpolecat"          %% "doobie-quill"         % doobieVersion,
  "org.http4s"            %% "http4s-dsl"           % http4sVersion,
  "org.http4s"            %% "http4s-blaze-server"  % http4sVersion,
  "org.http4s"            %% "http4s-blaze-client"  % http4sVersion,
  "org.http4s"            %% "http4s-circe"         % http4sVersion,
  "io.circe"              %% "circe-generic"        % circeVersion,
  "io.circe"              %% "circe-literal"        % circeVersion,
  "ch.qos.logback"         % "logback-classic"      % "1.2.3",
  "io.chrisdavenport"     %% "log4cats-slf4j"       % "1.1.1",
  "com.github.pureconfig" %% "pureconfig"           % "0.14.0",
)

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.1" cross CrossVersion.full)
enablePlugins(JavaAppPackaging)

mainClass in Compile := Some("quiz.bot.Main")

