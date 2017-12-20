package com.github.grahamcrowell.LuceneDelimited

import better.files.File
import org.apache.lucene.document.{Document, Field, TextField}

import scala.util.{Failure, Success, Try}

abstract class DataSourceAbs extends DataSourceTrait

abstract class DelimitedPhysicalFileBase(hash: String,
                                         file: File,
                                         delimiter: String,
                                         columnNames: IndexedSeq[String])
  extends DelimitedPhysicalFileTrait {
  override lazy val nameValueMapIterator: Iterator[Map[String, String]] = lineIndexIterator map {
    lineIndexPair: (String, Int) =>
      (columnNames, lineIndexPair._1.split(delimiter))
        .zipped.toMap
        // append lineNumber key value pair
        .updated("lineNumber", lineIndexPair._2.toString)
  }
  override lazy val luceneDocumentIterator: Iterator[Document] = nameValueMapIterator map {
    nameValueMap: Map[String, String] =>
      val document = new Document
      document.add(new TextField("parent_path", file.parent.path.toAbsolutePath.toString, Field.Store.YES))
      document.add(new TextField("filename", file.name, Field.Store.YES))
      document.add(new TextField("hash", hash, Field.Store.YES))
      nameValueMap.map {
        nameValuePair: (String, String) => new TextField(nameValuePair._1, nameValuePair._2, Field.Store.YES)
      }.foreach {
        textField: TextField => document.add(textField)
      }
      document
  }
  lazy val lineIndexIterator: Iterator[(String, Int)] = lineIterator.zipWithIndex
  override lazy val lineIterator: Iterator[String] = file.lineIterator
  override val absolutePath: String = file.path.toAbsolutePath.toString
}


case class DelimitedPhysicalFile(hash: String,
                                 file: File,
                                 delimiter: String,
                                 columnNames: IndexedSeq[String])
  extends DelimitedPhysicalFileBase(hash = hash, file = file, delimiter = delimiter, columnNames = columnNames) {
  def inferDelimiter: Option[String] = {
    val header = lineIterator.next()
    val lineSample = lineIterator.take(11).toIndexedSeq
    DelimitedPhysicalFile.delimiters.map {
      delimiter =>
        // count columns in header when split by delimiter
        val header_count = header.split(delimiter).length.toDouble
        // count columns in each sample line (first N lines of file) when split by delimiter
        val sample_counts = lineSample.map(_.split(delimiter).length.toDouble)
        // pseudo variance where header count is mean
        val header_dispersion = sample_counts.map {
          sample_count => Math.pow(header_count - sample_count, 2)
        }.sum / sample_counts.length
        // return tuple3 (triple) for each delimiter
        (delimiter, header_count, header_dispersion)
    }
      // filter out delimiters that don't occur in header (ie. header_count > 1.0)
      .filter(_._2 > 1.0)
      // filter out delimiters that occur in header different number of times than in sample data lines
      .filter(_._3 == 0.0)
      // all else being equal favour delimiter that occurs most
      .sortBy(_._2)
      // get delimiter(s)
      .map(_._1)
      // infer delimiter to be most common
      .headOption
  }
}

object DelimitedPhysicalFile {

  protected val delimiters: Seq[String] = Seq(",", "\t", "|")

  def apply(file: File,
            delimiter: String,
            columnNames: IndexedSeq[String]): DelimitedPhysicalFileBase = new DelimitedPhysicalFile(file.md5, file, delimiter, columnNames)

  def apply(file: File): DelimitedPhysicalFileBase = {

    val hash = Try(file.md5)
    val lineIterator = Try(file.lineIterator)
    val header = lineIterator.map(_.next)
    val lineSample = lineIterator.map(_.take(11))
    val inferredDelimiter = header.flatMap(head => lineSample.map(sample => inferDelimiter(head, sample)))
    val delimiterTry: Try[String] =
      inferredDelimiter match {
        case Success(v) => v
        case Failure(e) => throw new Exception(s"ooga booga: ${e.getMessage}")
      }
    val inferredColumnNames: Try[IndexedSeq[String]] = delimiterTry flatMap {
      case delimiter: String => header.map(_.split(delimiter).toIndexedSeq)
      case _ => Try(IndexedSeq())
    }
    hash.flatMap {
      hash =>
        delimiterTry.flatMap {
          delimiter =>
            inferredColumnNames.map {
              columnNames => new DelimitedPhysicalFile(hash, file, delimiter, columnNames)
            }
        }
    } match {
      case Success(v) => v
      case Failure(e) => throw new Exception(s"foo bar: ${e.getMessage}")
    }
  }

  def inferDelimiter(header: String, lineSample: Iterator[String]): Try[String] = {
    Try {
      DelimitedPhysicalFile.delimiters.map {
        delimiter =>
          // count columns in header when split by delimiter
          val header_count = header.split(delimiter).length.toDouble
          // count columns in each sample line (first N lines of file) when split by delimiter
          val sample_counts = lineSample.map(_.split(delimiter).length.toDouble)
          // pseudo variance where header count is mean
          val header_dispersion = sample_counts.map {
            sample_count => Math.pow(header_count - sample_count, 2)
          }.sum / sample_counts.length
          // return tuple3 (triple) for each delimiter
          (delimiter, header_count, header_dispersion)
      }
        // filter out delimiters that don't occur in header (ie. header_count > 1.0)
        .filter(_._2 > 1.0)
        // filter out delimiters that occur in header different number of times than in sample data lines
        .filter(_._3 == 0.0)
        // all else being equal favour delimiter that occurs most
        .sortBy(_._2)
        // get delimiter(s)
        .map(_._1)
        // infer delimiter to be most common
        .head
    }
  }

}