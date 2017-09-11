package tests

import com.github.mideo.sssh._
import com.jcraft.jsch.{JSchException, Session}
import org.mockito.Mockito._

class CommandExecutorSpec extends ssshSpec {

  private object commandExec extends CommandExecutor with MockJSch {

    override def runCommand(session: Session, command: String): Unit = {
      session.getHost
      session.setPassword(command)
    }
  }

  behavior of "CommandExecutor"

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
    commandExec("pass", "testAlias1")

    //Then
    (credentials filter (_.alias == "testAlias1")).par foreach {
      verify(session, times(1)).setUserInfo(_)
    }
    verify(session, times(1)).connect()
    verify(session, times(1)).disconnect()
    verify(session, times(1)).getHost
    verify(session, times(1)).setPassword("pass")
  }


  it should "thrown an error if run command fails" in {
    //Given
    val session = mock[Session]
    when(session.connect()).thenThrow(new JSchException("blah blah blah"))
    credentials = Credentials.from(testConfig)
    credentials foreach {
      credential: Credential => when(commandExec.sch.getSession(credential.user, credential.host)).thenReturn(session)
    }

    //When
    the[SSSHException] thrownBy {
      commandExec("pass", "testAlias1")

      //Then
    } should have message "Failed to Connect to host"



  }

}
