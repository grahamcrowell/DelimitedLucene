package com.github.grahamcrowell.LuceneDelimited

import better.files.File
import org.apache.lucene.document.Document
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.search.IndexSearcher

/**
  * Base class for a file object.  Built from Lucene query or from
  */
trait DataSource extends Comparable[DataSource] {
  val absolutePath: String
  val hash: String

  override def compareTo(o: DataSource): Int = {
    val this_tmp = this.absolutePath + this.hash
    val rhs = o.absolutePath + o.hash
    if (this_tmp < rhs)
      return -1
    if (this_tmp > rhs)
      return 1
    0
  }
}
trait DelimitedFile extends DataSource {
  val delimiter: String
  val columnNames: IndexedSeq[String]
}
trait DelimitedSourceFile extends DelimitedFile {
  val nameValueMapIterator: Iterator[Map[String, String]]
  val luceneDocumentIterator: Iterator[Document]
}
trait TenantSourceFileService {
  val delimitedSourceDataFileIterator: Iterator[DelimitedSourceDataFile]
}
trait TenantLuceneService {
  val tenantRootPath: String
  val tenantIndexWriter: IndexWriter
  val tenantIndexSearcher: IndexSearcher
}

trait SourceFile {
  val file: File
  val hash: Option[String]
  val folderDateStamp: Option[String]
  val filename: Option[String]
}

trait DelimitedSourceDataFile extends SourceFile {
  val delimiter: String
  val columnNames: IndexedSeq[String]
  val nameValueMapIterator: Iterator[Map[String, String]]
}
