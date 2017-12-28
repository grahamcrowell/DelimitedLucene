package com.github.grahamcrowell.LuceneDelimited

import better.files.File
import org.apache.lucene.document.Document
import org.apache.lucene.index.IndexWriter

trait FsFileProcessor {
  val file: File
  def processFile: Option[Iterator[Document]]
}

trait DelimitedFileLoader {
  val documentIterator: Iterator[Document]
  val indexWriter: IndexWriter
  def loadFile: Boolean
}

case class FsFileProcessImpl(file: File)


