import better.files.File

val file = File("/Users/gcrowell/Documents/git/DelimitedLucene/esldata/WFF_m1f/20170625/Absence.csv")
//val file = File("/Users/gcrowell/Documents/git/DelimitedLucene/esldata/WFF_not_delimited/19940314/not_delimited.txt")

val delimiter = ","
val lineIterator = file.lineIterator
val header = lineIterator.next()
//val lineSample = lineIterator.take(11).toIndexedSeq
//val hash = file.md5.take(8)

val columnNames = header.split(delimiter)
val lines = lineIterator.map {
  val tempLineIterator = file.lineIterator
  println(tempLineIterator.take(10).toIndexedSeq(5))
  data_line => columnNames.zip(data_line).toMap
}
protected val delimiters: Seq[String] = Seq(",", "\t", "|")

 val d = {
  val lineIterator = file.lineIterator
  val header = lineIterator.next()
  val lineSample = lineIterator.take(11).toIndexedSeq
  delimiters.map {
    delimiter =>
      // count columns in header when split by delimiter
      val header_count = header.split(delimiter).length.toDouble
      // count columns in each sample line (first N lines of file) when split by delimiter
      val sample_counts = lineSample.map(_.split(delimiter).length.toDouble)
      // pseudo variance where header count is mean
      val header_dispersion = sample_counts.map {
        sample_count => Math.pow(header_count - sample_count, 2)
      }.sum / sample_counts.length
      // return tuple3 (triple) for each delimiter
      (delimiter, header_count, header_dispersion)
  }
    // filter out delimiters that don't occur in header (ie. header_count > 1.0)
    .filter(_._2 > 1.0)
    // filter out delimiters that occur in header different number of times than in sample data lines
    .filter(_._3 == 0.0)
    // all else being equal favour delimiter that occurs most
    .sortBy(_._2)
    // get delimiter(s)
    .map(_._1)
    // infer delimiter to be most common
    .headOption
}