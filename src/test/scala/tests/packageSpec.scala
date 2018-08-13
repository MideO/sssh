package tests

import java.nio.file.Paths

import com.github.mideo.sssh._

class packageSpec extends ssshSpec {

  behavior of "ssh"

  it should "create single Credentials List" in {
    //Given
    val cred = Credential("testAlias", "testHost", "user", Some("pass"), Some(Paths.get("~/.ssh/")))

    //When
    credentials = cred

    //Then
    noException should be thrownBy {
      ensureCredentialsProvided()
    }
    credentials should be(List(cred))
  }


  it should "credentials mutate and access credentials" in {
    //Given
    val cred = Credentials.from(testConfig)

    //When
    credentials = cred

    //Then
    noException should be thrownBy {
      ensureCredentialsProvided()
    }
    credentials should be(cred)
  }

  it should "ensure credentials provided" in {
    //When
    credentials = List.empty

    //Then
    the [SSSHException]  thrownBy {
      ensureCredentialsProvided()
    } should have message ConfigError

  }
}
