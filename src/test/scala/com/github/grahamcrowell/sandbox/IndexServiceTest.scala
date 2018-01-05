package com.github.grahamcrowell.LuceneDelimited

import better.files.File
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}


class IndexServiceTest extends FunSpec with Matchers with BeforeAndAfter {

  val tenantRoot: File = File("./test_data/indexing_tests/small_tenant_root")
  var delimitedFile: DelimitedFileImpl = _
  var documentBuilder: DocumentBuilderImpl = _
  var root: DataRootWalker = _
  var indexer: IndexerService = _

  object DataRootWalker extends DataRootWalkerBase(tenantRoot)
  describe("IndexerTest") {
    before {
      delimitedFile = DelimitedFileImpl(File("./test_data/indexing_tests/small_tenant_root/20170625/Absence.csv"), Vector("AbsenceID", "EventDate", "EmployeeID", "AbsenceReason0", "AbsenceReason1", "AbsenceHours", "AbsenceDays", "FunctionalCategory", "PlanningCategory", "CompensationCategory"), ',')
      documentBuilder = DocumentBuilderImpl(delimitedFile)
      root = DataRootWalker
      indexer = IndexerService(root.dataRoot)
    }

    it("should find test data") {
      tenantRoot.children.isEmpty shouldBe false
    }
    it("should have an open indexWriter") {
      indexer.indexWriter.isOpen shouldBe true
    }
    it("should index each line (100) of a single file as a Document") {
      val init_doc_count = indexer.indexWriter.numDocs()
      indexer.indexDocuments(documentBuilder.documentBuilder)
      val end_doc_count = indexer.indexWriter.numDocs()
      val new_doc_count = end_doc_count - init_doc_count
      new_doc_count shouldBe 100
      indexer.indexWriter.close()
    }


    it("should indexDocuments of multiple files") {
      // @TODO test: indexDocuments of multiple files
    }

  }
}
