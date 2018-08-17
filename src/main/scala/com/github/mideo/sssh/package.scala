package com.github.mideo

import java.io.InputStream
import java.util.logging.Logger

import com.jcraft.jsch.ChannelExec

import scala.language.experimental.macros
import com.github.mideo.sssh.Implicits._

import scala.collection.mutable.ArrayBuffer


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
      channel.getErrStream.getBytes().read map {
        data => throw SSSHException(s"[${channel.getSession.getHost}] ${new String(data.toArray, 0, data.length)}")
      }

      val res = channel.getInputStream.getBytes().read match {
        case read:Option[ArrayBuffer[Byte]] if read.isDefined =>
          read map {
            data => val str = new String(data.toArray, 0, data.length)
            logger.info(s"[${channel.getSession.getHost}] \n$str")
            str
          }

        case read =>  throw SSSHException(s"[${channel.getSession.getHost}] could not read data from host ${read} ")
      }
      res.get
    }
  }


  object sudo extends Sudo

  object execute extends Execute

  object scpTo extends ScpTo

  object scpFrom extends ScpFrom

}
