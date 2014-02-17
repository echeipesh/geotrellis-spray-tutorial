name := "GeoTrellis Tutorial Project"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
	"com.azavea.geotrellis" %% "geotrellis" % "0.9.0-RC4",
  "com.azavea.geotrellis" %% "geotrellis-jetty" % "0.9.0-RC4"
)

resolvers += Resolver.sonatypeRepo("snapshots")
