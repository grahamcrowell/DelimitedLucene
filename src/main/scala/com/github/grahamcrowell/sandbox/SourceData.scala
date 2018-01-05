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
trait AbstractFile extends Comparable[AbstractFile] {
  val absolutePath: String
  val hash: String
  val lineIterator: Iterator[String]

  override def compareTo(o: AbstractFile): Int = {
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
  * Represents any delimited text file (physical and/or indexed).
  */
trait DelimitedFile extends AbstractFile {
  val delimiter: String
  val columnNames: IndexedSeq[String]
}

trait LuceneDocumentIterator {
  val luceneDocumentIterator: Iterator[Document]
}

trait DelimitedFsFileTrait extends DelimitedFile with LuceneDocumentIterator {
  val nameValueMapIterator: Iterator[Map[String, String]]
  protected val file: File
}

/**
  * Represents the root data folder of a tenant.
  * esldata/WFF_f0o
  */
trait TenantSourceFileServiceTrait {
  val tenantSourceRootPath: String
  val tenantIndexWriter: IndexWriter
  val delimitedSourceFileIterator: Iterator[DelimitedFsFile]
}


trait TenantLuceneService {
  val tenantRootPath: String
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
