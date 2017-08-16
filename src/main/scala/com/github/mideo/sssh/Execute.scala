package com.github.mideo.sssh

import com.jcraft.jsch.{ChannelExec, Session}

trait Execute
  extends CommandExecutor
    with RemoteSessionIO {

  override def runCommand(session: Session, command: String): Unit = {
    val channel: ChannelExec = session.openChannel("exec").asInstanceOf[ChannelExec]
    channel.setCommand(command)
    channel.connect()
    readChannelInputStream(channel)
    channel.disconnect()
  }
}