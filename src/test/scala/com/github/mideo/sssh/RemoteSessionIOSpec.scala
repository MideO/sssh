package com.github.mideo.sssh

import java.io.InputStream
import org.mockito.Mockito._
import com.jcraft.jsch.ChannelExec
import org.mockito.Matchers._

class RemoteSessionIOSpec extends ssshSpec {

  behavior of "RemoteSessionIOSpec"

  object remoteSessionIO extends RemoteSessionIO
  var channel: ChannelExec = _
  var in: InputStream = _

  before {
    channel = mock[ChannelExec]
    in = mock[InputStream]

  }

  it should "readInputStream when channel is not closed and inputStream is available" in {
    //Given
    when(channel.getInputStream).thenReturn(in)
    when(channel.isClosed).thenReturn(false, true)
    when(in.available()).thenReturn(5, 0)
    when(in.read()).thenReturn(Character.getNumericValue('H') * 6)
    when(in.read(any(classOf[Array[Byte]]), any(classOf[Int]), any(classOf[Int]))).thenReturn(6)

    //When
    val str = remoteSessionIO.readInputStream(channel)

    //Then
    verify(channel, times(1)).getInputStream
    verify(channel, times(2)).isClosed
    verify(in, times(2)).available()
    verify(in, times(1)).read(any(classOf[Array[Byte]]), any(classOf[Int]), any(classOf[Int]))
    str should not be empty
  }

  it should "not readInputStream when channel is closed and inputStream is available" in {
    //Given
    when(channel.getInputStream).thenReturn(in)
    when(channel.isClosed).thenReturn(true)

    //When
    val str = remoteSessionIO.readInputStream(channel)

    //Then
    verify(channel, times(1)).getInputStream
    verify(channel, times(1)).isClosed
    verify(in, times(0)).available()
    str should be(empty)
  }

  it should "not readInputStream when channel is not closed and inputStream is not available" in {
    //Given
    when(channel.getInputStream).thenReturn(in)
    when(channel.isClosed).thenReturn(false, true)
    when(in.available()).thenReturn(0)

    //When
    val str = remoteSessionIO.readInputStream(channel)

    //Then
    verify(channel, times(1)).getInputStream
    verify(channel, times(2)).isClosed
    verify(in, times(1)).available()
    str should be(empty)
  }

}
