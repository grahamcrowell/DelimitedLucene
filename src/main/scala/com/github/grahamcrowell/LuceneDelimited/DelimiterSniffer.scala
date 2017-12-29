package com.github.grahamcrowell.LuceneDelimited

import better.files.File

import scala.util.{Failure, Success, Try}

/**
  * Stores the computed metrics used to compare the validity of delimiters.
  *
  * @param headerCount            the number of tokens that result from splitting the first line on @param delimiter.  the number of column in the header accounting to this delimiter.
  * @param averageSampleCount     the average number of tokens that result from splitting the data lines on @param delimiter.  the average number of columns in the data lines accounting to this delimiter.
  * @param delimiterCountVariance the variation in the number of data line tokens relative to that of the header.  pseudo variance where header count is a proxy for the mean. valid delimited files have the same number of columns in the each data line.
  * @param delimiter              the character used to separate lines of data into tokens (ie labels and values).
  */
case class DelimiterVarianceMetric(headerCount: Int,
                                   averageSampleCount: Double,
                                   delimiterCountVariance: Double,
                                   delimiter: Char) {
  /**
    * a valid delimiter will split both the header and the data lines into multiple tokens.
    * ie. headerCount > 1 and averageSampleCount > 1
    */
  // @todo rename to describe meaning.
  lazy val isNonZero: Boolean = {
    this match {
      case DelimiterVarianceMetric(header_count, _, _, _) if header_count <= 1 => false
      case DelimiterVarianceMetric(_, average_sample_count, _, _) if average_sample_count <= 1 => false
      case _ => true
    }
  }
  /**
    * a valid token will split the header and the data line into the same number of tokens
    */
  lazy val isConsistent: Boolean = {
    if (headerCount == averageSampleCount & delimiterCountVariance == 0) true
    else false
  }
  /**
    * a valid token is both non-zero and consistent
    */
  lazy val isValid: Boolean = {
    isConsistent & isNonZero
  }
}

object DelimiterVarianceMetricHelper extends Ordering[DelimiterVarianceMetric] {
  override def compare(lhs: DelimiterVarianceMetric, rhs: DelimiterVarianceMetric): Int = {
    if (lhs.isValid & rhs.isValid) {
      0
    } else if (lhs.isValid) {
      1
    } else if (rhs.isValid) {
      -1
    } else {
      0
    }
  }
}

trait DelimiterSniffer {
  /**
    * @return
    */
  lazy val sniffDelimiter: Option[Char] = {
    implicit val comparison = DelimiterVarianceMetricHelper
    val delimiter_variances: Seq[DelimiterVarianceMetric] = possibleDelimiters.map {
      possible_delimiter => computeDelimiterVariance(possible_delimiter)
    }.filter((metric: DelimiterVarianceMetric) => metric.isValid & metric.isNonZero)
    if (delimiter_variances.isEmpty) None else Option(delimiter_variances.max.delimiter)
  }
  val header: String
  val lineSample: IndexedSeq[String]
  val possibleDelimiters: List[Char]
  /**
    * @return
    */
  val computeDelimiterVariance: Char => DelimiterVarianceMetric = (testDelimiter: Char) => {
    val header_count = header.count(_ == testDelimiter)
    val lineList = lineSample.toList
//    lineList.foreach(println)
//    println(s"lineList: $lineList")
    val sample_counts = lineList.map(_.count(_ == testDelimiter).toDouble)
//    println(s"sample_counts: $sample_counts")
    val sample_count_average = sample_counts.foldRight(0.0)(_ + _) / sample_counts.length
//    println(s"sample_count_average: $sample_count_average")

    val delimiter_count_variance = sample_counts.map {
      sample_count => Math.pow(header_count - sample_count, 2.0)
    }.sum / sample_counts.size
    DelimiterVarianceMetric(header_count, sample_count_average, delimiter_count_variance, testDelimiter)
  }

  def columnNames: Option[IndexedSeq[String]] = {
    sniffDelimiter.map((delimiter: Char) => header.split(delimiter).toIndexedSeq)
  }
}

case class DelimiterSnifferImpl(header: String, lineSample: IndexedSeq[String])
  extends DelimiterSniffer {
  override val possibleDelimiters: List[Char] = {
    List('|', ',', '\t')
  }
}

object DelimiterSniffer {
  def sniffFile(file: File): Option[DelimitedFile] = {
    val lineIterator = file.lineIterator

    val headerTry: Option[String] = Try {
      lineIterator.next()
    } match {
      case Success(header) => Option(header)
      case Failure(exception) => None
    }
    if (headerTry.isEmpty) return None
//    println("headerTry")
//    println(headerTry.get)

    val lineSampleTry = Try {
      lineIterator.take(5).toIndexedSeq
    } match {
      case Success(lineSample) => Option(lineSample)
      case Failure(exception) => None
    }
    if (lineSampleTry.isEmpty) return None
//    println("lineSampleTry")
//    println(lineSampleTry.get)

    val delimiterSniffer = DelimiterSnifferImpl(headerTry.get, lineSampleTry.get)
//    println("delimiterSniffer")

    val delimiter = delimiterSniffer.sniffDelimiter
    if (delimiter.isEmpty) return None
//    println("delimiter")
//    println(delimiter.get)

    val columnNames = delimiterSniffer.columnNames
    if (columnNames.isEmpty) return None
//    println("columnNames")
//    println(columnNames.get)
    Option(DelimitedFileImpl(file, columnNames.get, delimiter.get))
  }
}

trait DelimitedFile {
  val file: File
  val columnNames: IndexedSeq[String]
  val delimiter: Char
}

case class DelimitedFileImpl(file: File,
                             columnNames: IndexedSeq[String],
                             delimiter: Char)
  extends DelimitedFile
