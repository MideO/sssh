package com.github.mideo.sssh

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

abstract class ssshSpec
  extends FlatSpec
    with Matchers
    with BeforeAndAfter
    with MockitoSugar {
  val configString =
    """
    ssh {
      credentials = [
        {
          alias = testhost
          host = myhostname
          user = username
          password = changeme
        },
        {
          alias = testhost1
          host = myhostname1
          user = username1
          password = changeme1
        }
      ]
    }
    """
  val testConfig: Config = ConfigFactory.parseString(configString)
}
