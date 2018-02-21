package tests

import com.github.mideo.sssh.{Credential, Credentials}
import com.typesafe.config.{ConfigException, ConfigFactory}

class CredentialSpec extends ssshSpec {
  behavior of "Credential"

  val credentials: List[Credential] = Credentials.from(testConfig)

  it should "load Credentials List" in {
    credentials should have size 2
  }

  it should "throw ConfigException if ssh not in config" in {
    a[ConfigException] should be thrownBy Credentials.from(ConfigFactory.load())
  }

  it should "have correct values" in {
    val credential: Credential = credentials.head
    credential.host should equal("myhostname")
    credential.alias should equal("testAlias")
    credential.user should equal("username")
    credential.pass.get should equal("changeme")
  }

  it should "provide credential " in {
    val credential: Credential = credentials.head
    val password = credential.pass.get
    "credential.showMessage(\"blah blah\")" should compile
    credential.getPassphrase should equal(credential.pass.get)
    credential.getPassword should equal(credential.pass.get)
    credential.promptPassword(password) should be(true)
    credential.promptPassphrase(password) should be(true)
    credential.promptYesNo(password) should be(true)
    credential.promptKeyboardInteractive("zzz", "xcx", "ddfd", Array("sdsd"), Array(x = true)) should equal(Array(credential.user, credential.pass.get))
  }
}
