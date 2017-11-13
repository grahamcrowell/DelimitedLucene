package com.github.grahamcrowell.indexer

import org.apache.lucene.document.{Document, Field, TextField}
import org.apache.lucene.index.IndexWriter

trait LuceneServiceTrait {

}


object LuceneService {
  def writeToDoc[A <: DelimitedDataFileTrait](delimitedDataFile: A, indexWriter: IndexWriter): Unit = {
    println("*****Indexing: " + delimitedDataFile.file.pathAsString)

    delimitedDataFile.nameValueMapIterator.foreach(
      lineNameValueMap => {
        var doc = new Document()
        lineNameValueMap.foreach {
          named_pairs => {
            val field = new TextField(named_pairs._1, named_pairs._2, Field.Store.YES)
            doc.add(field)
          }
        }
        indexWriter.addDocument(doc)
      }
    )

    indexWriter.commit()
    indexWriter.close()
    println("*****Completed: " + delimitedDataFile.file.pathAsString)
  }
}

