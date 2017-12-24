package com.github.grahamcrowell.LuceneDelimited

import better.files.File
import org.scalatest.{FunSpec, Matchers}

class DelimiterSnifferTest extends FunSpec with Matchers {

  val test_data_root: File = File("./test_data/delimiter_inference_tests")

  case class DelimiterSnifferImpl(header: String,
                                  lineSample: TraversableOnce[String],
                                  possibleDelimiters: List[Char]) extends DelimiterSniffer


  describe("DelimiterSnifferTest") {

    it("should computeDelimiterVariance") {
      val comma_delimited: File = test_data_root / "comma_delimited.txt"
      val lineIterator = comma_delimited.lineIterator
      val header = lineIterator.next
      val lineSample = lineIterator
      val possibleDelimiters = List(',', '|')
      val sniffer = DelimiterSnifferImpl(header, lineSample, possibleDelimiters)
      assert(sniffer.computeDelimiterVariance(',') == DelimiterVarianceMetric(6, 6.0, 0, ','))
    }

    it("should sniffDelimiter") {
      val comma_delimited: File = test_data_root / "comma_delimited.txt"
      val lineIterator = comma_delimited.lineIterator
      val header = lineIterator.next
      val lineSample = lineIterator
      val possibleDelimiters = List(',', '|')
      val sniffer = DelimiterSnifferImpl(header, lineSample, possibleDelimiters)
      sniffer.sniffDelimiter should equal (',')
    }

  }
}
