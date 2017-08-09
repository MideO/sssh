package com.github.mideo.sssh

import java.io.OutputStream

import com.jcraft.jsch.{ChannelExec, Session}


object sudo
  extends CommandExecutor
    with RemoteSessionIO {
  override def runCommand(session: Session, command: String): Unit = {
    val channel: ChannelExec = session.openChannel("exec").asInstanceOf[ChannelExec]
    channel.setCommand("sudo -S -p '' " + command)

    val out: OutputStream = channel.getOutputStream
    channel.setErrStream(System.err)
    channel.connect()
    out.write(s"${session.getUserInfo.getPassword}\n" .getBytes)
    out.flush()
    readInputStream(channel)
    channel.disconnect()
  }
}
