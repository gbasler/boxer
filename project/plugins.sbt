resolvers ++= Seq(
    "Nexus Public" at "http://nexus/nexus/content/groups/public",
    "Nexus Releases" at "http://nexus/nexus/content/repositories/releases",
    "Nexus Snapshots" at "http://nexus/nexus/content/repositories/snapshots",
	"Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/releases/"
)

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")
