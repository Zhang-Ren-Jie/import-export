name := "import-export"

version := "1.0"

scalaVersion := "2.11.8"

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")

  (System.getProperty("os.name"),System.getProperty("user.name"),"ping -n 2 192.168.1.10 "!) match {
    case (_,_,_) =>
      System.setProperty("config.file","./src/main/config/application.conf")
  }
}



libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "1.6.0",
  "org.apache.hbase" % "hbase-client" % "1.1.9",
  "org.apache.hbase" % "hbase-common" % "1.1.9",
  "org.apache.hbase" % "hbase-server" % "1.1.9",
  "com.typesafe" % "config" % "1.3.1"
).map(
  _.excludeAll(ExclusionRule(organization = "org.mortbay.jetty"))
)

assemblyMergeStrategy in assembly := {
  case PathList(ps @ _*) if ps.last endsWith "pom.properties" => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

assemblyExcludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
  cp filter { c =>
    c.data.getName == "kryo-2.21.jar"
    c.data.getName == "guava-14.0.1.jar" ||
    c.data.getName =="jsp-api-2.1-6.1.14.jar" ||
    c.data.getName == "javax.servlet-3.0.0.v201112011016.jar" ||
       //c.data.getName == "servlet-api-2.5-6.1.14.jar" ||
    c.data.getName == "commons-beanutils-1.7.0.jar" ||
    c.data.getName == "commons-collections-3.2.2.jar" ||
    c.data.getName == "jcl-over-slf4j-1.7.10.jar" ||
    c.data.getName == "hadoop-yarn-common-2.2.0.jar" ||
    c.data.getName == "jsp-2.1-6.1.14.jar" ||
    c.data.getName == "kryo-2.21.jar" ||
    c.data.getName == "spark-core_2.11-1.6.0.jar" ||
    c.data.getName == "spark-launcher_2.11-1.6.0.jar" ||
    c.data.getName == "unused-1.0.0.jar"

  }
}

