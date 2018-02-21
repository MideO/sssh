package com.github.mideo.sssh

import java.nio.file.{Path, Paths}

import com.jcraft.jsch.{UIKeyboardInteractive, UserInfo}
import com.typesafe.config.Config

import scala.collection.JavaConverters._

case class Credential(alias: String, host: String, user: String, pass: Option[String], identity: Option[Path])
  extends UserInfo
    with UIKeyboardInteractive {
  val password: String = pass.getOrElse("")

  override def showMessage(message: String): Unit = println(message)

  override def promptPassword(message: String): Boolean = true

  override def promptYesNo(message: String): Boolean = true

  override def promptPassphrase(message: String): Boolean = true

  override def getPassphrase: String = password

  override def getPassword: String = password

  override def promptKeyboardInteractive(destination: String, name: String, instruction: String, prompt: Array[String], echo: Array[Boolean]): Array[String] = Array(user, password)
}

object Credentials {
  def from(config: Config): List[Credential] = {
    val configList: List[Config] = config.getConfigList("ssh.credentials").asScala.toList
    configList map {
      config: Config => {
        val alias = config.getString("alias")
        val host = config.getString("host")
        val user = config.getString("user")
        val password = if (config.hasPath("password")) Option(config.getString("password")) else None
        val identity = if (config.hasPath("identity")) Option(Paths.get(config.getString("identity"))) else None
        Credential(alias, host, user, password, identity)
      }
    }
  }
}

