package com.github.mideo.sssh

import java.io.Closeable

object ResourceManaged {
  def Try[T](fun: T)(implicit closeable: Closeable): T = {
    try
      fun
    finally {
      closeable.close()
    }

  }

}
