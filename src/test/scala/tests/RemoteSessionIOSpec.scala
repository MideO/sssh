package tests

import java.io.InputStream

import com.github.mideo.sssh.{RemoteSessionIO, SSSHException}
import com.jcraft.jsch.{ChannelExec, Session}
import org.mockito.Matchers._
import org.mockito.Mockito._



class RemoteSessionIOSpec extends ssshSpec {

  behavior of "RemoteSessionIO"

  object remoteSessionIO extends RemoteSessionIO

  it should "readInputStream when channel is not closed and inputStream is available" in {
    //Given
    val channel = mock[ChannelExec]
    val in = mock[InputStream]
    val err = mock[InputStream]

    val session = mock[Session]
    when(channel.getSession).thenReturn(session)
    when(session.getHost).thenReturn("Fugazzi Host")
    when(channel.getInputStream).thenReturn(in)
    when(channel.getErrStream).thenReturn(err)

    when(in.available()).thenReturn(5, 0)
    when(in.read()).thenReturn(Character.getNumericValue('H') * 6)
    when(in.read(any(), any(), any())).thenReturn(6)

    //When
    val str = remoteSessionIO.readChannelInputStream(channel)

    //Then
    verify(channel, times(1)).getInputStream
    verify(in, times(1)).read(any(), any(), any())
    str should be(empty)
  }

  it should "throw an error is readInputStream has errors" in {
    //Given
    val channel = mock[ChannelExec]
    val in = mock[InputStream]
    val err = mock[InputStream]
    val session = mock[Session]
    when(channel.getSession).thenReturn(session)
    when(session.getHost).thenReturn("Fugazzi Host")
    when(channel.getErrStream).thenReturn(err)

    when(err.read()).thenReturn(Character.getNumericValue('H') * 6)
    when(err.read(any(),any(), any())).thenReturn(9)
    when(channel.getInputStream).thenReturn(in)

    //When
    val thrown = the[SSSHException] thrownBy {
      remoteSessionIO.readChannelInputStream(channel)
    }

    //Then
    thrown.getMessage should startWith ("[Fugazzi Host] ")

  }

}
