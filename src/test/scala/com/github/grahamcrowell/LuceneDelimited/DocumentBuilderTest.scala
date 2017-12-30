package com.github.grahamcrowell.LuceneDelimited

import better.files.File
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

class DocumentBuilderTest extends FunSpec with BeforeAndAfter with Matchers {
  var delimitedFile: DelimitedFileImpl = _
  var documentBuilder: DocumentBuilderImpl = _
  var expected : Map[String, String] = _

  describe("DocumentBuilderTest") {
    before {
      delimitedFile = DelimitedFileImpl(File("./test_data/indexing_tests/mock_tenant_root/20170625/Absence.csv"), Vector("AbsenceID", "EventDate", "EmployeeID", "AbsenceReason0", "AbsenceReason1", "AbsenceHours", "AbsenceDays", "FunctionalCategory", "PlanningCategory", "CompensationCategory"), ',')
      documentBuilder = DocumentBuilderImpl(delimitedFile)
      expected = Map("AbsenceID" -> "Absence-1", "PlanningCategory" -> "Scheduled", "AbsenceReason1" -> "Reason1", "FunctionalCategory" -> "Sick Leave", "AbsenceHours" -> "24.0", "AbsenceReason0" -> "Unpaid Sick Leave Scheduled", "CompensationCategory" -> "Unpaid", "line_number" -> "2", "AbsenceDays" -> "3.0", "EventDate" -> "2012-01-01", "EmployeeID" -> "Employee-1850")
    }
    it("should delimitedFile") {
      documentBuilder.delimitedFile shouldBe DelimitedFileImpl(File("./test_data/indexing_tests/mock_tenant_root/20170625/Absence.csv"), Vector("AbsenceID", "EventDate", "EmployeeID", "AbsenceReason0", "AbsenceReason1", "AbsenceHours", "AbsenceDays", "FunctionalCategory", "PlanningCategory", "CompensationCategory"), ',')
    }

    it("should nameValueMapIterator") {
      documentBuilder.nameValueMapIterator.hasNext shouldBe true
      val actual = documentBuilder.nameValueMapIterator.next
      expected should contain theSameElementsAs actual
    }
    it("should documentBuilder") {
      documentBuilder.documentBuilder.hasNext shouldBe true
      val actual = documentBuilder.documentBuilder.next
      expected.foreach {
        keyValue => actual.get(keyValue._1) shouldBe keyValue._2
      }
      actual.get("parent_path") shouldBe "/Users/gcrowell/Documents/git/DelimitedLucene/test_data/indexing_tests/mock_tenant_root/20170625"
      actual.get("filename") shouldBe "Absence.csv"
      actual.get("hash") shouldBe "AA6F049365C69303E50AFD411E7EC067"
      actual.get("line_number") shouldBe "2"
    }
  }
}
