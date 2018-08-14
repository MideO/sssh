package tests

import java.io.{ByteArrayInputStream, InputStream, OutputStream}
import java.nio.file.{Files, Paths, StandardOpenOption}

import com.github.mideo.sssh._
import com.jcraft.jsch.{ChannelExec, Session}
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import Implicits._

import scala.collection.mutable.ArrayBuffer

class scpToSpec extends ssshSpec {
  object testScpTo
    extends ScpTo
    with MockJSch

  behavior of "scpTo"
  val data: Array[Byte] = "Fugazzi".getBytes()
  override def beforeAll() {
    Files.write(Paths.get("testFile.txt"), data, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
  }

  override def afterAll {
    Files.delete(Paths.get("testFile.txt"))
  }

  it should "runCommand" in {
      //Given
      val session = mock[Session]
      val channel = mock[ChannelExec]
      val out = mock[OutputStream]
      val in = mock[InputStream]

      credentials = Credentials.from(testConfig)
      credentials foreach {
        credential: Credential => when(testScpTo.sch.getSession(credential.user, credential.host)).thenReturn(session)
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
     // when (in getBytes()).thenReturn(ReadProgress[InputStream](Some(ArrayBuffer(data:_*)), 6))
      when(session.getUserInfo).thenReturn(credentials.head)
      val fileName = "testFile.txt"

      //When
      testScpTo(fileName)

      //Then
      verify(session, times(credentials.size)).connect()
      verify(session, times(credentials.size)).openChannel("exec")
      verify(channel, times(credentials.size)).getOutputStream
      verify(channel, times(credentials.size)).setCommand(s"scp -t  $fileName $fileName")
      verify(out, times(credentials.size)).write(Array.emptyByteArray, 0, 0)

      verify(out, times(credentials.size*2)).flush()
      verify(session, times(credentials.size)).disconnect()

  }

  it should "runCommand on InputStream" in {

    //Given
    val session = mock[Session]
    val channel = mock[ChannelExec]
    val out = mock[OutputStream]
    val in = mock[InputStream]

    credentials = Credentials.from(testConfig)
    credentials foreach {
      credential: Credential => when(testScpTo.sch.getSession(credential.user, credential.host)).thenReturn(session)
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
    testScpTo(fileName, new ByteArrayInputStream(data))

    //Then
    verify(session, times(credentials.size)).connect()
    verify(session, times(credentials.size)).openChannel("exec")
    verify(channel, times(credentials.size)).getOutputStream
    verify(channel, times(credentials.size)).setCommand(s"scp -t  $fileName")
    verify(out, times(credentials.size)).write(Array.emptyByteArray, 0, 0)

    verify(out, times(credentials.size*2)).flush()
    verify(session, times(credentials.size)).disconnect()

  }
}

