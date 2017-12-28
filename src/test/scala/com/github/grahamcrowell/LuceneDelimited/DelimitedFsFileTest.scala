package com.github.grahamcrowell.LuceneDelimited

import better.files.File
import org.scalatest.{BeforeAndAfter, FunSpec}

class DelimitedFsFileTest extends FunSpec with BeforeAndAfter {

  var delimitedFsFile: DelimitedFsFile = _
  val file_full_path = "./test_data/indexing_tests/mock_tenant_root/20170625/Agency_Retained.csv"

  describe("DelimitedFsFileTest") {
    it("should exist") {
      val file = File(file_full_path)
      assert(file.isRegularFile)
    }
    it("should have > 11 lines") {
      val file = File(file_full_path)
      val lineIterator = file.lineIterator
      assert(lineIterator.nonEmpty)
      val header = lineIterator.next
      assert(header.nonEmpty)
      val sampleLine = lineIterator.take(10)
      assert(sampleLine.size == 10)
    }
    it("should have potential delimiters to test") {
      assert(DelimitedFsFile.delimiters.contains(","))
      assert(DelimitedFsFile.delimiters.contains("|"))
      assert(DelimitedFsFile.delimiters.contains("\t"))
    }
    it("should infer the delimiter from a line sample") {
      val file = File(file_full_path)
      val lineIterator = file.lineIterator
      val header = lineIterator.next
      val sampleLines = lineIterator.take(10)
      val delimiter = DelimitedFsFile.inferDelimiter(header, sampleLines)
      assert(delimiter.isSuccess)
      assert(delimiter.get == ",")
    }


    it("should apply from a file") {
//      assert(delimitedFsFile.file.isRegularFile)
//      assert(delimitedFsFile.isInstanceOf[DelimitedFsFile])
    }

    it("should have a line iterator that includes the header") {
      val iter = delimitedFsFile.lineIterator
      assert(iter.nonEmpty)
      assert(iter.next == "Agency_Retained-1,Requisition-28,2012-02-20,Manpower - IT:Hire Manufacturing Applications Specialist - North America : Requisition-28,Manpower,Staffing Solutions,25000.0,0.1")
      assert(iter.size == 132)
    }
  }
}
