package com.github.grahamcrowell.LuceneDelimited

import org.apache.lucene.document.{Document, Field, TextField}

trait DocumentBuilder {
  val delimitedFile: DelimitedFile

  def documentIterator: Iterator[Document] = {
    nameValueMapIterator map { nameValueMap =>
//      println("documentBuilder")
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
