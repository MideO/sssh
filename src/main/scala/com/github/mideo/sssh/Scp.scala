package com.github.mideo.sssh

import java.io.{Closeable, InputStream, OutputStream}
import java.nio.file.{Files, Paths}

import com.jcraft.jsch.{ChannelExec, Session}

import scala.collection.mutable.ArrayBuffer
import Implicits._

sealed trait Scp
  extends CommandExecutor
    with RemoteSessionIO {

  def scpCommand: String

  def writeFile(in: InputStream, out: OutputStream, command: String): Unit

  override def apply(fileName: String): Unit = execute(s"$scpCommand $fileName $fileName")

  override def apply(fileName: String, localFileName: String): Unit = {
    execute(s"$scpCommand $fileName $localFileName")
  }

  override def runCommand(session: Session, command: String): Unit = {
    val channel: ChannelExec = session.openChannel("exec").asInstanceOf[ChannelExec]
    channel.setCommand(command)
    val out: OutputStream = channel.getOutputStream
    val in: InputStream = channel.getInputStream
    channel.connect()
    implicit val closeable: Closeable = out
    ResourceManaged.Try {
      writeFile(in, out, command)
    }
  }

}

trait ScpTo extends Scp {
  override def scpCommand: String = "scp -t "

  override def writeFile(in: InputStream, out: OutputStream, command: String): Unit = {

      val fileName: String = command.split(" ").last.split("/").last
      (in getBytes()).read foreach {
        data: ArrayBuffer[Byte] =>
          out.write(s"C0644 ${data.length} $fileName \n".getBytes())
          out.flush()
          in.read(data.toArray, 0, data.length)
          out.write(data.toArray, 0, data.length)
          out.flush()
      }
  }

  def apply(fileName: String, in: InputStream): Unit = {
    execute(s"$scpCommand $fileName")
  }
}


trait ScpFrom extends Scp {
  override def scpCommand: String = "scp -f "

  override def writeFile(in: InputStream, out: OutputStream, command: String): Unit = {
    val fileName: String = command.split(" ").last
    val data: Option[ArrayBuffer[Byte]] = (in getBytes()).read
    data foreach { d: ArrayBuffer[Byte] => out.write(d.toArray); out.flush() }
    data map { d => Files.write(Paths.get(fileName), d.toArray) }

  }
}