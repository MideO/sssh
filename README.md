##  sssh

[![Build Status](https://travis-ci.org/MideO/sssh.svg?branch=master)](https://travis-ci.org/MideO/sssh)

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
          password = changeme1
        }
      ]
    }
    """
 
  sssh.credentials = sssh.Credentials.from(ConfigFactory.load("default.conf"))
  
  //or Create credentials directly
  sssh.credentials = Credential("testAlias", "testHost", "user", "pass", "~/.ssh/")
    
  //execute command
  sssh.execute("pwd")
  
  //execute scpFrom remote file
   sssh.scpFrom("wget-log")
  
  //or Create credentials directly from List
  sssh.credentials = List(Credential("testAlias", "testHost", "user", "pass", "~/.ssh/id_rsa.pub"), Credential("testAlias1", "testHost1", "user1", "pass1", ""))
  
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