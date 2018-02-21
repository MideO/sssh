##  sssh

[![Build Status](https://travis-ci.org/MideO/sssh.svg?branch=master)](https://travis-ci.org/MideO/sssh)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.mideo/sssh_2.11/badge.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.mideo%22%20a%3A%22sssh_2.11%22)


A Simple scala ssh library

```scala
import java.io.InputStream
import com.typesafe.config.ConfigFactory

object main extends App {
 //Create credentials from config
  val configString =
    """
    ssh {
      credentials = [
        {
          alias = testhost
          host = myhostname
          user = username
          password = changeme
        },
        {
          alias = testhost1
          host = myhostname1
          user = username1
          identity = "path/to/key.pem"
        }
      ]
    }
    """
 
  sssh.credentials = sssh.Credentials.from(ConfigFactory.load("default.conf"))
  
  //or configLike String 
  sssh.credentials = sssh.Credentials.from(ConfigFactory.parseString(configString))
  
  //or Create credentials directly
  sssh.credentials = Credential("testAlias", "testHost", "user", Some("pass"), Some(Paths.get(".ssh/key.pem"))
 
    
  //execute command
  sssh.execute("pwd")
  
  //execute scpFrom remote file
   sssh.scpFrom("wget-log")
  
  //or Create credentials directly from List
  sssh.credentials = List(Credential("testAlias", "testHost", "user", Some("pass"), Some(Paths.get("PATH/To/key.pub"))), Credential("testAlias1", "testHost1", "user1", Some("pass1"), None))
  
  //execute command on single host
  sssh.execute("pwd", "testAlias")

  //execute scpTo for file in current working directory
  sssh.scpTo("build.sbt")
  
  //execute scpTo from input stream 
  val in: InputStream = getClass.getClassLoader.getResourceAsStream("default.conf")
  sssh.scpTo("default.conf", in)

  //execute sudo command
  sssh.sudo("whoami")
}

```
