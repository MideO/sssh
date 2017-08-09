package com.github.mideo.sssh

import com.jcraft.jsch.{JSch, Session}

trait CommandExecutor {

  val sch: JSch = new JSch

  def runCommand(session: Session, command: String): Unit

  private def execute(command:String, credential: Credential): Unit ={
    val session: Session = sch.getSession(credential.user, credential.host)
    session.setUserInfo(credential)
    session.connect()
    runCommand(session, command)
    session.disconnect()
  }

  def execute(command: String): Unit = {
    credentials.par foreach {
      credential: Credential => {
        execute(command, credential)
      }
    }
  }

  def execute(command: String, host:String): Unit = {
    (credentials filter (_.alias == host)).par foreach {
      credential: Credential => {
        execute(command, credential)
      }
    }
  }

  def apply(command: String): Unit = {
    ensureCredentialsProvided()
    execute(command)
  }

  def apply(command: String, host:String): Unit = {
    ensureCredentialsProvided()
    execute(command, host)
  }
}

