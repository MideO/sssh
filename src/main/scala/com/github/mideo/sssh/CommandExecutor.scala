package com.github.mideo.sssh

import java.nio.file.Path

import com.jcraft.jsch.{JSch, Session}

trait CommandExecutor {

  val sch: JSch = new JSch

  def runCommand(session: Session, command: String): Unit

  private def execute(command:String, credential: Credential): Unit ={
    Option(credential.identity) match {
      case p:Path => sch.addIdentity(p.toAbsolutePath.toString)
      case _ => //NoOp
    }
    val session: Session = sch.getSession(credential.user, credential.host)
    credential.identity match {
      case Some(identity) => sch.addIdentity(identity.toAbsolutePath.toString)
      case None => None
    }

    session.setUserInfo(credential)
    try{
      session.connect()
    } catch {
      case t: Throwable => throw SSSHException(s"Failed to Connect to host: $t")
    }
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

  def execute(command: String, alias:String): Unit = {
    (credentials filter (_.alias == alias)).par foreach {
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

