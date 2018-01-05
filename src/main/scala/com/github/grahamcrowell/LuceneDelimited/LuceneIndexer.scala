package com.github.grahamcrowell.LuceneDelimited

import java.nio.file.Path

import better.files.File
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.store.FSDirectory

trait DocumentIndexer {
  val delimitedDataRootPath: File

  def indexDocumentIterator(documents: Iterator[Document]): Long
}

abstract class DocumentIndexerBase
  extends DocumentIndexer {
  lazy private[this] val analyzer = new StandardAnalyzer()
  lazy private[this] val writerConfig = new IndexWriterConfig(analyzer)
  lazy private[this] val luceneIndexRootFolder: File = delimitedDataRootPath / luceneFolderName
  lazy private[this] val fsDirectory: FSDirectory = FSDirectory.open(luceneIndexRootFolder.path)
  lazy private[this] val indexWriter = new IndexWriter(fsDirectory, writerConfig)
  writerConfig.setOpenMode(OpenMode.CREATE_OR_APPEND)
  writerConfig.setRAMBufferSizeMB(500)
  writerConfig.setCommitOnClose(true)

  override def indexDocumentIterator(documents: Iterator[Document]): Long = {
    var indexCount = 0
    documents.foreach {
      document => {
        indexWriter.addDocument(document)
        indexCount += 1
      }
    }
    indexCount
  }
}

case class DocumentIndexerImpl(delimitedDataRootPath: File)
  extends DocumentIndexerBase

trait TenantDataRoot {
  val rootPath: Path
}

