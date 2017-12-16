package com.github.grahamcrowell.LuceneDelimited

import better.files.File
import org.apache.lucene.document.Document
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.search.IndexSearcher

/**
  * Base class representing any file (physical and/or indexed).
  * Isn't necessarily delimited.
  * Can be built from Lucene query or from File system
  */
trait DataSourceTrait extends Comparable[DataSourceTrait] {
  val absolutePath: String
  val hash: String
  override def compareTo(o: DataSourceTrait): Int = {
    val this_tmp = this.absolutePath + this.hash
    val rhs = o.absolutePath + o.hash
    if (this_tmp < rhs)
      return -1
    if (this_tmp > rhs)
      return 1
    0
  }
}

/**
  * Adds
  */
trait DelimitedFile extends DataSourceTrait {
  val delimiter: String
  val columnNames: IndexedSeq[String]
}
trait DelimitedPhysicalFileTrait extends DelimitedFile {
  protected val file: File
  val nameValueMapIterator: Iterator[Map[String, String]]
  val luceneDocumentIterator: Iterator[Document]
}

/**
  * Represents the root data folder of a tenant.
  * esldata/WFF_f0o
  */
trait TenantSourceFileService {
  val delimitedSourceFileIterator: Iterator[DelimitedPhysicalFileTrait]
}
trait TenantLuceneService {
  val tenantRootPath: String
  val tenantIndexWriter: IndexWriter
  val tenantIndexSearcher: IndexSearcher
}


//
//trait SourceFile {
//  val file: File
//  val hash: Option[String]
//  val folderDateStamp: Option[String]
//  val filename: Option[String]
//}
//
//trait DelimitedSourceDataFile extends SourceFile {
//  val delimiter: String
//  val columnNames: IndexedSeq[String]
//  val nameValueMapIterator: Iterator[Map[String, String]]
//}
