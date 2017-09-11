package tests

import java.io.InputStream

import com.github.mideo.sssh.{InputStreamVerifier, SSSHException}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.prop.TableDrivenPropertyChecks._


class InputStreamVerifierSpec extends ssshSpec {
  var in: InputStream = _
  before {
    in = mock[InputStream]
  }

  object inputStreamVerifier extends InputStreamVerifier

  behavior of "InputStreamVerifier"

  it should "verifyDataInputStream with default offset" in {
    val dataTable = Table(
      ("mocked", "expected"),
      (0, 0),
      (-1, -1),
      (4, 4),
      (8, 8),
      (99, 99)
    )
    forAll(dataTable) { (mocked, expected) =>
      //Given
      when(in.read).thenReturn(mocked)

      //When
      val result = inputStreamVerifier.verifyDataInputStream(in)

      //Then
      result should equal(expected)
    }
  }

  it should "verifyDataInputStream with given offset" in {
    //Given
    when(in.read(any(classOf[Array[Byte]]), any(classOf[Int]), any(classOf[Int]))).thenReturn(0)

    //When
    val result = inputStreamVerifier.verifyDataInputStream(in, 99)

    //Then
    result should equal(0)
  }

  it should "verifyDataInputStream with default offset should throw runtime exception if contains errors" in {
    val dataTable = Table(
      "mocked",
      1,
      2
    )
    forAll(dataTable) {
      mocked =>
        //When
        when(in.read).thenReturn(mocked, 'P'.toInt, 'l'.toInt, 'a'.toInt, 't'.toInt, 'y'.toInt, 'p'.toInt, 'u'.toInt, 's'.toInt, '\n'.toInt)
        when(in.available()).thenReturn(1)

        //Then
        the[SSSHException] thrownBy {
          inputStreamVerifier.verifyDataInputStream(in)
        } should have message "Platypus"
    }
  }

  it should "verifyDataInputStream with given offset should throw runtime exception if contains errors" in {

    //When
    when(in.available()).thenReturn(1)
    when(in.read(any(classOf[Array[Byte]]), any(classOf[Int]), any(classOf[Int]))).thenReturn(1)
    when(in.read).thenReturn('P'.toInt, 'l'.toInt, 'a'.toInt, 't'.toInt, 'y'.toInt, 'p'.toInt, 'u'.toInt, 's'.toInt, '\n'.toInt)

    //Then
    the[SSSHException] thrownBy {
      inputStreamVerifier.verifyDataInputStream(in, 10)
    } should have message "Platypus"
  }

}
