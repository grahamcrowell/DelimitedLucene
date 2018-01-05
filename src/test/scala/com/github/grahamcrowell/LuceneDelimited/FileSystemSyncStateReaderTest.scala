package com.github.grahamcrowell.LuceneDelimited

import better.files.File
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

class FileSystemSyncStateReaderTest extends FunSpec with Matchers with BeforeAndAfter {

  val root_folder = File("/Users/gcrowell/Documents/git/DelimitedLucene/test_data/indexing_tests/mock_tenant_root")
  var fileSystemSyncStateReader: FileSystemSyncStateReader = _
  describe("FileSystemSyncStateReaderTest") {

    before {
      fileSystemSyncStateReader = FileSystemSyncStateReader(root_folder)
    }
    it("should readSyncState") {
      val syncState = fileSystemSyncStateReader.readSyncState
      syncState.foreach(println)
      syncState.map {
        x => {
          File(x.root_abspath) / x.parent_relpath / x.filename
        }.isRegularFile
      }.foldLeft(true)(_ & _) shouldBe (true)
      syncState should not be empty
    }

  }
}
