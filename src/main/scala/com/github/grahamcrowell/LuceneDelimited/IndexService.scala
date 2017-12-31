package com.github.grahamcrowell.LuceneDelimited

import better.files.File
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.store.FSDirectory

trait IndexerService {
  val indexWriter: IndexWriter

  def indexDocument(document: Document): Boolean = {
    indexWriter.addDocument(document)
    //    indexWriter.commit()
    true
  }

  def indexDocuments(documents: Iterator[Document]): Boolean = {
    println("indexing documents")
    println(s"is empty ${documents.isEmpty}")

    //    documents.foreach {
    //      document => {
    //        println(document.get("line_number"))
    //      }
    //    }
    documents.foreach {
      document => {
        println(s"indexing: ${document.get("filename")} (line: ${document.get("line_number")})")
        indexWriter.addDocument(document)
      }
    }
    indexWriter.flush()
    indexWriter.commit()
//    indexWriter.close()
    true
  }
}

case class IndexerImpl(indexWriter: IndexWriter) extends IndexerService

object IndexerService {
  def apply(dataRoot: File): IndexerService = {
    val luceneIndexFolder = dataRoot / ".Lucene"
    println(s"Lucene index data folder: ${luceneIndexFolder.pathAsString}")
    val indexDirectory = FSDirectory.open(luceneIndexFolder.path)
    val analyzer = new StandardAnalyzer()
    val writerConfig = new IndexWriterConfig(analyzer)
    writerConfig.setOpenMode(OpenMode.CREATE_OR_APPEND)
    writerConfig.setRAMBufferSizeMB(500)
    writerConfig.setCommitOnClose(true)
    val indexWriter = new IndexWriter(indexDirectory, writerConfig)
    IndexerImpl(indexWriter)
  }
}