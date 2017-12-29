package com.github.grahamcrowell.LuceneDelimited

import better.files.File
import org.scalatest.{FunSpec, Matchers}

// @todo add tests for1 other test_data files
class DelimiterSnifferTest extends FunSpec with Matchers {

  val test_data_root: File = File("./test_data/delimiter_inference_tests")

  case class DelimiterSnifferImpl(header: String,
                                  lineSample: IndexedSeq[String],
                                  possibleDelimiters: List[Char]) extends DelimiterSniffer


  describe("DelimiterSnifferTest") {

    it("should computeDelimiterVariance for a csv") {
      val comma_delimited: File = test_data_root / "comma_delimited.txt"
      val lineIterator = comma_delimited.lineIterator
      val header = lineIterator.next
      val lineSample = lineIterator.take(10).toIndexedSeq
      val possibleDelimiters = List(',', '|', '\t')
      val sniffer = DelimiterSnifferImpl(header, lineSample, possibleDelimiters)
      assert(sniffer.computeDelimiterVariance(',') == DelimiterVarianceMetric(6, 6.0, 0, ','))
      assert(sniffer.computeDelimiterVariance('|') == DelimiterVarianceMetric(0, 0.0, 0, '|'))
      assert(sniffer.computeDelimiterVariance('\t') == DelimiterVarianceMetric(0, 0.0, 0, '\t'))
      sniffer.sniffDelimiter should be (Option(','))
    }

    it("should computeDelimiterVariance for a pipe") {
      val comma_delimited: File = test_data_root / "pipe_delimited.txt"
      val lineIterator = comma_delimited.lineIterator
      val header = lineIterator.next
      val lineSample = lineIterator.take(10).toIndexedSeq
      val possibleDelimiters = List(',', '|', '\t')
      val sniffer = DelimiterSnifferImpl(header, lineSample, possibleDelimiters)
      assert(sniffer.computeDelimiterVariance('|') == DelimiterVarianceMetric(6, 6.0, 0, '|'))
      assert(sniffer.computeDelimiterVariance(',') == DelimiterVarianceMetric(0, 0.0, 0, ','))
      assert(sniffer.computeDelimiterVariance('\t') == DelimiterVarianceMetric(0, 0.0, 0, '\t'))
      sniffer.sniffDelimiter should be (Option('|'))
    }

    it("should computeDelimiterVariance for a tab") {
      val comma_delimited: File = test_data_root / "tab_delimited.txt"
      val lineIterator = comma_delimited.lineIterator
      val header = lineIterator.next
      val lineSample = lineIterator.take(10).toIndexedSeq
      val possibleDelimiters = List(',', '|', '\t')
      val sniffer = DelimiterSnifferImpl(header, lineSample, possibleDelimiters)
      assert(sniffer.computeDelimiterVariance('\t') == DelimiterVarianceMetric(6, 6.0, 0, '\t'))
      assert(sniffer.computeDelimiterVariance(',') == DelimiterVarianceMetric(0, 0.0, 0, ','))
      assert(sniffer.computeDelimiterVariance('|') == DelimiterVarianceMetric(0, 0.0, 0, '|'))
      sniffer.sniffDelimiter should be (Option('\t'))
    }

    it("should skip non-delimited files") {
      val comma_delimited: File = test_data_root / "not_text.zip"
      val sniffed = DelimiterSniffer.sniffFile(comma_delimited)
      sniffed should equal(None)
    }

    it("should return a DelimitedFile") {
      val comma_delimited: File = test_data_root / "comma_delimited.txt"
      val expected = DelimitedFileImpl(comma_delimited, IndexedSeq("Column1", "Column2", "Column3", "Column4", "Column5", "Column6", "Column7"), ',')
      val sniffed = DelimiterSniffer.sniffFile(comma_delimited)
      sniffed should equal(Option(expected))
    }
  }
}
