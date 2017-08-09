package com.github.mideo.sssh

import com.jcraft.jsch.{ChannelExec, Session}

object execute
  extends CommandExecutor
    with RemoteSessionIO {

  override def runCommand(session: Session, command: String): Unit = {
    val channel: ChannelExec = session.openChannel("exec").asInstanceOf[ChannelExec]
    channel.setCommand(command)
    channel.connect()
    readInputStream(channel)
    channel.disconnect()
  }
}