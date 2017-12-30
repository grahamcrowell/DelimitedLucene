package com.github.grahamcrowell.LuceneDelimited

import better.files.File
import org.scalatest.{FunSpec, Matchers}

class IndexerTest extends FunSpec with Matchers {

  describe("IndexerTest") {
    val tenantRoot: File = File("./test_data/indexing_tests/mock_tenant_root")

    it("should find test data") {
      tenantRoot.children.isEmpty shouldBe false
    }
    it("should indexDocument") {
      object DataRootWalker extends DataRootWalkerBase(tenantRoot)
      val root = DataRootWalker
      val indexer = Indexer(root.dataRoot)

      val delimitedFile = root.delimitedFiles
      val documentIter = delimitedFile.map(DocumentBuilderImpl)
      documentIter.flatMap(_.documentBuilder).map(indexer.indexDocument)
//        .flatMap(indexer.indexDocument())
//      indexer.indexDocument(delimitedFile.next)
    }

    it("should indexWriter") {

    }

    it("should indexDocuments") {

    }

  }
}
