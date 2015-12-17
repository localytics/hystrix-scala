publishTo := Some("hystrix-scala-release" at "http://localytics.artifactoryonline.com/localytics/maven-local")

credentials += Credentials(Path.userHome / ".sbt" / "credentials" / "localytics_artifactory.props")

releaseSettings
