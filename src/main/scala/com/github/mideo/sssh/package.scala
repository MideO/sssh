package com.github.mideo

import java.io.InputStream
import java.util.logging.Logger

import com.jcraft.jsch.ChannelExec

import scala.language.experimental.macros


package object sssh {

  case class SSSHException(private val message: String = "",
                           private val cause: Throwable = None.orNull) extends RuntimeException(message, cause)

  val configError = "Credentials must be set to run commands, set credentials like such: credentials = Credentials(<Config>)"
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
      throw SSSHException(configError)
    }
  }

  trait RemoteSessionIO {

    val logger:Logger = Logger.getLogger(classOf[RemoteSessionIO].getName)
    def readChannelInputStream(channel: ChannelExec): String = {
      val in = channel.getInputStream
      var str: String = ""

      val buffer: Array[Byte] = Array.fill[Byte](1024)(0)

      while (!channel.isClosed) {
        while (in.available() > 0) {
          val i: Int = in.read(buffer, 0, 1024)
          if (i <= 0) { in.close();return str}
          str = new String(buffer, 0, i)
          logger.info(s"[${channel.getSession.getHost}\n$str")
        }
      }


      str
    }
  }

  trait InputStreamVerifier {
    def verifyDataInputStream(in: InputStream, offset: Int = 0): Int = {

      var buffer: Int = offset match {
        case 0 => in.read
        case _ => in.read(Array.fill[Byte](1024)(0), 0, offset)
      }

      buffer match {
        case _@(0 | -1) => buffer
        case _@(1 | 2) =>
          val sb = new StringBuffer
          while (buffer.toChar != '\n' && in.available() > 0) {
            sb.append(buffer.toChar)
            buffer = in.read
          }

          throw SSSHException(sb.toString.trim)
        case _ => buffer
      }
    }
  }

  object sudo extends Sudo

  object execute extends Execute

  object scpTo extends ScpTo

  object scpFrom extends ScpFrom



}
