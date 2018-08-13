package com.github.mideo.sssh

import java.io.InputStream

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

object Implicits {

  case class ReadProgress[T <: InputStream](read: Option[ArrayBuffer[Byte]], position: Int)

  implicit class PimpedInputStream[T <: InputStream](inputStream: T) {
    private final val MaxBuffer: Int = 5118

    @tailrec final def getBytes(buffer: ArrayBuffer[Byte] = new ArrayBuffer[Byte]): ReadProgress[T] = {
      val contentBuffer: Array[Byte] = Array.fill[Byte](buffer.length + MaxBuffer)(Byte.MinValue)

      inputStream.read(contentBuffer, buffer.length, MaxBuffer) match {
        case i: Int if i <= 0 => ReadProgress(None, i)
        case i: Int if i > 0 => ReadProgress(Some(buffer ++= contentBuffer.filter(!_.equals(Byte.MinValue))), buffer.length)
        case _ => getBytes(buffer)
      }

    }
  }

}
