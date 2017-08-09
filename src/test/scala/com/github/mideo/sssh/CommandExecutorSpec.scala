package com.github.mideo.sssh
import com.jcraft.jsch.{JSch, Session}
import org.mockito.Mockito._

class CommandExecutorSpec extends ssshSpec {


  object commandExec extends CommandExecutor {
    override val sch: JSch = mock[JSch]
    override def runCommand(session: Session, command: String): Unit = {
      session.getHost
      session.setPassword(command)
    }
  }

  behavior of "CommandExecutorSpec"

  it should "run command on all hosts" in {
    //Given
    val session = mock[Session]
    credentials = Credentials.from(testConfig)
    credentials foreach {
      credential: Credential => when(commandExec.sch.getSession(credential.user, credential.host)).thenReturn(session)
    }

    //When
    commandExec("pass")

    //Then
    credentials foreach {
      verify(session, times(1)).setUserInfo(_)
    }
    verify(session, times(credentials.size)).connect()
    verify(session, times(credentials.size)).disconnect()
    verify(session, times(credentials.size)).getHost
    verify(session, times(credentials.size)).getHost
    verify(session, times(credentials.size)).setPassword("pass")
  }

  it should "run command single host" in {
    //Given
    val session = mock[Session]
    credentials = Credentials.from(testConfig)
    credentials foreach {
      credential: Credential => when(commandExec.sch.getSession(credential.user, credential.host)).thenReturn(session)
    }

    //When
    commandExec("pass", "testhost1")

    //Then
    (credentials filter (_.alias == "testhost1")).par foreach {
      verify(session, times(1)).setUserInfo(_)
    }
    verify(session, times(1)).connect()
    verify(session, times(1)).disconnect()
    verify(session, times(1)).getHost
    verify(session, times(1)).getHost
    verify(session, times(1)).setPassword("pass")

  }

}
