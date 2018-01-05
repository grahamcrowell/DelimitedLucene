package com.github.grahamcrowell.LuceneDelimited

import better.files.File
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.collection.JavaConverters._

class FileSystemSyncElementTest extends FunSpec with Matchers with BeforeAndAfter {

  val root_folder = File("/Users/gcrowell/Documents/git/DelimitedLucene/test_data/indexing_tests/mock_tenant_root")
  var fileSystemSyncElement: FileSystemSyncElement = _
  describe("FileSystemSyncElementTest") {

    before {

    }

    it("should parent_relpath") {
      root_folder.walk(2)
        .filter(_.isRegularFile)

      val iter = root_folder.path.iterator().asScala
      iter.foreach(println)
    }

    it("should modified_timestamp") {

    }

    it("should filename") {

    }

    it("should hash_digest") {

    }

    it("should root_abspath") {

    }

    it("should fileSystemSyncElementBuilder") {

    }

  }
}
