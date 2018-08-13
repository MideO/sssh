package com.github.mideo

import java.io.InputStream
import java.util.logging.Logger

import com.jcraft.jsch.ChannelExec

import scala.language.experimental.macros


package object sssh {

  case class SSSHException(private val message: String = "",
                           private val cause: Throwable = None.orNull) extends RuntimeException(message, cause)

  val ConfigError = "Credentials must be set to run commands, set credentials like such: credentials = Credentials(<Config>)"
  private var _credentials: List[Credential] = List.empty

  def credentials: List[Credential] = _credentials

  def credentials_=(c: List[Credential]) {
    _credentials = c
  }

  def credentials_=(c: Credential) {
    _credentials = List(c)
  }

  def ensureCredentialsProvided(): Unit = {
    if (credentials.size <= 0) {
      throw SSSHException(ConfigError)
    }
  }

  trait RemoteSessionIO {

    val logger: Logger = Logger.getLogger(classOf[RemoteSessionIO].getName)

    def readChannelInputStream(channel: ChannelExec): String = {
      val in = channel.getInputStream
      val err: InputStream = channel.getErrStream
      var str: String = ""
      val buffer: Array[Byte] = Array.fill[Byte](1024)(0)

      val errDataLength = err.read(buffer)
      if (errDataLength > 0) {
        in.close()
        err.close()
        throw SSSHException(s"[${channel.getSession.getHost}] ${new String(buffer, 0, errDataLength)}")
      }


      while (in.available() > 0) {
        val i: Int = in.read(buffer, 0, 1024)
        str = new String(buffer, 0, i)
        logger.info(s"[${channel.getSession.getHost}] \n$str")
      }
      str
    }
  }


  object sudo extends Sudo

  object execute extends Execute

  object scpTo extends ScpTo

  object scpFrom extends ScpFrom

}
