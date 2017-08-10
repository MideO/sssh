package com.github.mideo.sssh

import java.io.{InputStream, OutputStream}
import java.nio.file.{Files, Paths}

import com.jcraft.jsch.{ChannelExec, Session}

import scala.collection.mutable.ArrayBuffer

sealed trait Scp
  extends CommandExecutor
    with RemoteSessionIO
    with InputStreamVerifier {
  val TwoMegaBytes = 2097152
  var localFile:String = new String
  def scpCommand:String

  def checkFileSizeCanBeCopied(size:Int): Unit ={
    if (TwoMegaBytes < size) {
      throw SshException("File to large to copy")
    }
  }

  def writeFile(in: InputStream, out: OutputStream, command: String):Unit

  override def apply(fileName: String): Unit = execute(s"$scpCommand $fileName")

  override def apply(fileName: String, localFileName:String): Unit = {
    localFile = localFileName
    execute(s"$scpCommand $fileName")
  }

  override def runCommand(session: Session, command: String): Unit = {
    val channel: ChannelExec = session.openChannel("exec").asInstanceOf[ChannelExec]
    channel.setCommand(command)
    val out: OutputStream = channel.getOutputStream
    val in: InputStream = channel.getInputStream
    channel.connect()
    writeFile(in, out, command)
  }

}

object scpTo extends Scp {

  override def scpCommand: String = "scp -t "

  var data: Array[Byte] = ArrayBuffer[Byte]().toArray

  override def writeFile(in: InputStream, out: OutputStream, command: String):Unit= {
    val fileName: String = localFile match {
      case "" => command.split(" ").last
      case _ => localFile
    }
    if (data.length == 0) {
      data = Files.readAllBytes(Paths.get(fileName))
    }

    checkFileSizeCanBeCopied(data.length)

    out.write(s"C0644 ${data.length} ${fileName.split("/").last} \n".getBytes())
    out.flush()
    verifyDataInputStream(in)

    out.write(data, 0, data.length)
    out.flush()
  }

  def apply(fileName: String, in:InputStream): Unit = {
    data = Array.fill[Byte](in.available())(0)
    in.read(data)
    execute(s"$scpCommand $fileName")
  }
}


object scpFrom extends Scp {

  override def scpCommand: String = "scp -f "

  override def writeFile(in: InputStream, out: OutputStream, command: String): Unit = {
    val fileName: String = localFile match {
      case empty => command.split(" ").last
      case _ => localFile
    }
    var buffer: Array[Byte] = Array.fill[Byte](1024)(0)
    out.write(buffer, 0, 1024)
    out.flush()
    var i: Int = in.read(buffer)

    var fileSize = new String(buffer).split(" ")(1).toInt
    checkFileSizeCanBeCopied(fileSize)
    var data: Array[Byte] = ArrayBuffer[Byte]().toArray

    var MaxBufferSize = 1024 * 4

    while (fileSize > 0) {
      if(MaxBufferSize > fileSize) {
        MaxBufferSize = fileSize
      }
      fileSize = fileSize - MaxBufferSize
      buffer = Array.fill[Byte](MaxBufferSize)(0)

      i = in.read(buffer, 0, MaxBufferSize)

      out.write(buffer)
      out.flush()

      if (i>0) data = data ++ buffer

    }

    Files.write(Paths.get(fileName), data)

  }
}