package tests

import com.github.mideo.sssh.CommandExecutor
import com.jcraft.jsch.JSch
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FlatSpec, Matchers}

abstract class ssshSpec
  extends FlatSpec
    with Matchers
    with BeforeAndAfter
    with BeforeAndAfterAll
    with MockitoSugar {

  trait MockJSch extends CommandExecutor{
    override val sch: JSch = mock[JSch]
  }

  val configString =
    """
    ssh {
      credentials = [
        {
          alias = testAlias
          host = myhostname
          user = username
          password = changeme
        },
        {
          alias = testAlias1
          host = myhostname1
          user = username1
          password = changeme1
        }
      ]
    }
    """
  val testConfig: Config = ConfigFactory.parseString(configString)
}
