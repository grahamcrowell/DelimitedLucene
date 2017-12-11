package com.github.grahamcrowell.indexer

import better.files.File
import org.scalatest.{BeforeAndAfter, FunSpec}

class DelimitedDataFileTest extends FunSpec with BeforeAndAfter {

  describe("DelimitedDataFileTest") {

    it("should inferDelimiter") {
      val tenantRoot = TenantRoot(esldata / "WFF_m1f")
      val sampleFolder: DatedDataFolderTrait = tenantRoot.datedFolders.next()
      val sampleFile: File = sampleFolder.file.children.next()
      println(s"sampleFile: ${sampleFile.pathAsString}")
      val delimiter = DelimitedDataFile.inferDelimiter(sampleFile)
      println(s"delimiter: $delimiter")
    }
  }
}
