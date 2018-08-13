package tests

import java.io.ByteArrayInputStream
import scala.collection.mutable.ArrayBuffer

import com.github.mideo.sssh.Implicits._

class PimpedInputStreamLikeSpec extends ssshSpec {

  behavior of "PimpedInputStreamLike"

  it should "read bytes" in {
    // Given
    val bytes: ArrayBuffer[Byte] = ArrayBuffer("lalalala".getBytes:_*)
    val bis = new ByteArrayInputStream(bytes.toArray)

    // When
    val res = bis getBytes()

    // Then
    res.isInstanceOf[ReadProgress[ByteArrayInputStream]] should equal(true)
    res.read should equal(Some(bytes))
    res.position should equal(bytes.length)
  }

  it should "read and append bytes" in {
    // Given
    val bytes: ArrayBuffer[Byte] = ArrayBuffer("lalalala".getBytes:_*)
    val resB: ArrayBuffer[Byte] = ArrayBuffer("lalalalalalalala".getBytes:_*)
    val bis = new ByteArrayInputStream(bytes.toArray)

    // When
    val res = bis getBytes bytes

    // Then
    res.isInstanceOf[ReadProgress[ByteArrayInputStream]] should equal(true)
    res.read should equal(Some(resB))
    res.position should equal(resB.length)

  }


  it should "read empty byte if inputStream is empty" in {
    val bis = new ByteArrayInputStream(Array.emptyByteArray)
    val res = bis getBytes new ArrayBuffer[Byte]

    // Then
    res.isInstanceOf[ReadProgress[ByteArrayInputStream]] should equal(true)
    res.read should equal(None)
    res.position should equal(-1)
  }

}
