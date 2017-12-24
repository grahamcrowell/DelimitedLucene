package com.github.grahamcrowell.LuceneDelimited

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
  val header: String
  val lineSample: Traversable[String]
  val possibleDelimiters: List[Char]

  /**
    *
    * @param header
    * @param lineSample
    * @return
    */
  def sniffDelimiter(header: String, lineSample: Traversable[String]): Char = {
    val delimiter_variance_computer = computeDelimiterVariance(header, lineSample) _
    val delimiter_variances = possibleDelimiters.map {
      possible_delimiter => delimiter_variance_computer(possible_delimiter)
    }.sorted(DelimiterVarianceMetricHelper)
    delimiter_variances.head.delimiter
  }

  /**
    *
    * @param header
    * @param lineSample
    * @param testDelimiter
    * @return
    */
  def computeDelimiterVariance(header: String,
                               lineSample: Traversable[String])
                              (testDelimiter: Char): DelimiterVarianceMetric = {
    val header_count = header.count(_ == testDelimiter)
    val sample_counts = lineSample.map(_.count(_ == testDelimiter).toDouble)
    val sample_count_average = sample_counts.sum / sample_counts.size
    val delimiter_count_variance = sample_counts.map {
      sample_count => Math.pow(header_count - sample_count, 2.0)
    }.sum / sample_counts.size
    DelimiterVarianceMetric(header_count, sample_count_average, delimiter_count_variance, testDelimiter)
  }
}
