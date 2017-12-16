package com.github.grahamcrowell.LuceneDelimited

import better.files.File
import org.apache.lucene.document.Document

abstract class DataSourceAbs extends DataSourceTrait

abstract class DelimitedPhysicalFileBase(hash: String,
                                         file: File,
                                         delimiter: String,
                                         columnNames: IndexedSeq[String])
  extends DelimitedPhysicalFileTrait {
  override lazy val nameValueMapIterator: Iterator[Map[String, String]] = lineIterator map {
    line: String => (columnNames, line.split(delimiter)).zipped.toMap
  }
  override lazy val luceneDocumentIterator: Iterator[Document] = _
  private lazy val lineIterator: Iterator[String] = file.lineIterator
  override val absolutePath: String = file.path.toAbsolutePath.toString

  def foo(x: Int)(y: Int): Double = {
    0.0
  }

}

case class DelimitedPhysicalFile(hash: String,
                                 file: File,
                                 delimiter: String,
                                 columnNames: IndexedSeq[String],
                                )
  extends DelimitedPhysicalFileBase(hash = hash, file = file, delimiter = delimiter, columnNames = columnNames) {


}