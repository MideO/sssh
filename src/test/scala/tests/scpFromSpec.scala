package tests

import java.io.{InputStream, OutputStream}

import com.github.mideo.sssh._
import com.jcraft.jsch.{ChannelExec, Session}
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}


class scpFromSpec extends ssshSpec {
  private object testScpFrom
    extends ScpFrom
    with MockJSch

  behavior of "scpFrom"

  it should "runCommand" in {

    //Given
    val session = mock[Session]
    val channel = mock[ChannelExec]
    val out = mock[OutputStream]
    val in = mock[InputStream]

    credentials = Credentials.from(testConfig)
    credentials foreach {
      credential: Credential => when(testScpFrom.sch.getSession(credential.user, credential.host)).thenReturn(session)
    }
    when(channel.getSession).thenReturn(session)
    when(session.getHost).thenReturn("Fugazzi Host")
    when(session.openChannel("exec")).thenReturn(channel)
    when(channel.getOutputStream).thenReturn(out)
    when(channel.getInputStream).thenReturn(in)
    when(channel.isClosed).thenReturn(false, true)
    when(in.available()).thenReturn(5, 0)
    when(in.read()).thenReturn(Character.getNumericValue('H') * 6)
    when(in.read(any(classOf[Array[Byte]]), any(classOf[Int]), any(classOf[Int]))).thenReturn(6)
    when(session.getUserInfo).thenReturn(credentials.head)
    val fileName = "testFile.txt"

    //When
    testScpFrom(fileName)

    //Then
    verify(session, times(credentials.size)).connect()
    verify(session, times(credentials.size)).openChannel("exec")
    verify(channel, times(credentials.size)).getOutputStream
    verify(channel, times(credentials.size)).setCommand(s"scp -f  $fileName")
    verify(out, times(credentials.size)).write(Array.fill[Byte](1024)(0), 0, 1024)

    verify(out, times(credentials.size)).flush()
    verify(session, times(credentials.size)).disconnect()

  }

}
