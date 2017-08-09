package com.github.mideo.sssh

import com.jcraft.jsch.{UIKeyboardInteractive, UserInfo}
import com.typesafe.config.Config

import scala.collection.JavaConverters._

case class Credential(alias: String, host: String, user: String, pass: String, identity: String)
  extends UserInfo
    with UIKeyboardInteractive {
  override def showMessage(message: String): Unit = println(message)

  override def promptPassword(message: String): Boolean = true

  override def promptYesNo(message: String): Boolean = true

  override def promptPassphrase(message: String): Boolean = true

  override def getPassphrase: String = pass

  override def getPassword: String = pass

  override def promptKeyboardInteractive(destination: String, name: String, instruction: String, prompt: Array[String], echo: Array[Boolean]): Array[String] = Array(user, pass)
}

object Credentials {
  def from(config: Config): List[Credential] = {
    val configList: List[Config] = config.getConfigList("ssh.credentials").asScala.toList
    configList map {
      config: Config => {
        val alias = config.getString("alias")
        val host = config.getString("host")
        val user = if (config.hasPath("user")) config.getString("user") else ""
        val password = if (config.hasPath("password")) config.getString("password") else ""
        val identity = if (config.hasPath("identity")) config.getString("identity") else ""
        Credential(alias, host, user, password, identity)
      }
    }
  }
}

