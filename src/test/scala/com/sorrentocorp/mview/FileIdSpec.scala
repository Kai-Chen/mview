package com.sorrentocorp.mview

import org.scalatest._

class FileIdSpec extends FlatSpec with Matchers {
  "FileId.apply" should "generate different ids for different paths" in {
    FileId("src").id should not be FileId("target").id
  }

  it should "generate the same id for the same path" in {
    FileId("src").id shouldBe FileId("src").id
  }
}
