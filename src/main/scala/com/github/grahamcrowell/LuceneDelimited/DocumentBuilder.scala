package com.github.grahamcrowell.LuceneDelimited

import better.files.File
import org.apache.lucene.document.{Document, Field, TextField}
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.store.NIOFSDirectory

trait DocumentBuilder {
  val delimitedFile: DelimitedFile

  def documentBuilder: Iterator[Document] = {
    nameValueMapIterator map { nameValueMap =>
      val document = new Document()
      document.add(new TextField("parent_path", delimitedFile.file.parent.path.toAbsolutePath.toString, Field.Store.YES))
      document.add(new TextField("filename", delimitedFile.file.name, Field.Store.YES))
      document.add(new TextField("hash", delimitedFile.file.md5, Field.Store.YES))
      document.add(new TextField("line_number", nameValueMap("line_number"), Field.Store.YES))
      delimitedFile.columnNames.foreach { columnName =>
        document.add(new TextField(columnName, nameValueMap.getOrElse(columnName,""), Field.Store.YES))
      }
      document
    }
  }

  def nameValueMapIterator: Iterator[Map[String, String]] = {
    delimitedFile.file.lineIterator.zipWithIndex.drop(1).map { lineStringNumber =>
      delimitedFile.columnNames.zip(lineStringNumber._1.split(delimitedFile.delimiter)).toMap + ("line_number" -> (lineStringNumber._2 + 1).toString)
    }
  }
}

case class DocumentBuilderImpl(delimitedFile: DelimitedFile) extends DocumentBuilder

trait Indexer {
  val indexWriter: IndexWriter

  def indexDocument(document: Document): Boolean = {
    indexWriter.addDocument(document)
    //    indexWriter.commit()
    true
  }

  def indexDocuments(documents: Iterator[Document]): Boolean = {
    documents.map {
      indexWriter.addDocument(_)
    }
    indexWriter.commit()
    true
  }
}

case class IndexerImpl(indexWriter: IndexWriter) extends Indexer

object Indexer {
  def apply(dataRoot: File): Indexer = {
    val indexDirectory = new NIOFSDirectory(dataRoot.path)
    val indexWriterConfig = new IndexWriterConfig()
    val indexWriter = new IndexWriter(indexDirectory, indexWriterConfig)
    IndexerImpl(indexWriter)
  }
}