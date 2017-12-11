package com.github.grahamcrowell.indexer

import better.files.File
import org.apache.logging.log4j.scala.Logging
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.{Document, Field, LongPoint, TextField}
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import org.apache.lucene.store.FSDirectory

// @TODO reuse Document and Field instance to reduce GC (@SEE https://wiki.apache.org/lucene-java/ImproveIndexingSpeed)
trait DocumentBuilder extends Logging {
  // path of lucene root data folder
  val indexDataDirectory: File
  // lucene index data files stored under this root folder
  private lazy val luceneIndexDirectory = FSDirectory.open(indexDataDirectory.path)

  def writeToDoc[DelimitedDataSource <: DelimitedDataFileTrait](delimitedDataFile: DelimitedDataSource): IndexedSeq[Document] = {
    var line_number: Long = 1
    delimitedDataFile.nameValueMapIterator.map {
      lineNameValueMap => {
        line_number = line_number + 1
        val doc = new Document()
        // meta data
        doc.add(new TextField("_relative_parent_path", delimitedDataFile.parent_relative_path, Field.Store.YES))
        doc.add(new TextField("_filename", delimitedDataFile.file.name, Field.Store.YES))
        doc.add(new TextField("_md5_hash_prefix", delimitedDataFile.hash, Field.Store.YES))
        doc.add(new LongPoint("_line_number", line_number))
        doc.add(new TextField("_line_number_str", line_number.toString, Field.Store.YES))
        // each column in source data is stored as a Lucene Field
        lineNameValueMap.foreach {
          named_pairs => {
            doc.add(new TextField(named_pairs._1, named_pairs._2, Field.Store.YES))
          }
        }
        doc
      }
    }
  }.toIndexedSeq
}




// @TODO reuse Document and Field instance to reduce GC (@SEE https://wiki.apache.org/lucene-java/ImproveIndexingSpeed)
trait LuceneIndexServiceTrait extends Logging {
  // path of lucene root data folder
  val indexDataDirectory : File
  // lucene index data files stored under this root folder
  private lazy val luceneIndexDirectory = FSDirectory.open(indexDataDirectory.path)
  /**
    * @see https://stackoverflow.com/questions/39442110/search-using-thread-in-lucene-6-2-using-scala

    * @param delimitedDataFile
    * @tparam DelimitedDataSource
    */
  // load data file into index

  def writeToDoc[DelimitedDataSource <: DelimitedDataFileTrait](delimitedDataFile: DelimitedDataSource): Unit = {
    logger.info("*****Indexing: " + delimitedDataFile.file.pathAsString)
    val analyzer = new StandardAnalyzer()
    // writeConfig cannot be reused
    val writerConfig = new IndexWriterConfig(analyzer)
    writerConfig.setOpenMode(OpenMode.CREATE_OR_APPEND)
    writerConfig.setRAMBufferSizeMB(500)
    // main index builder object
    val indexWriter = new IndexWriter(luceneIndexDirectory, writerConfig)
    // track line number of each row (ie Lucuene Document)
    var line_number: Long = 1
    delimitedDataFile.nameValueMapIterator.foreach(
      lineNameValueMap => {
        line_number = line_number + 1
        var doc = new Document()
        // add row meta data to index
        val parent_relative_path_field = new TextField("_relative_parent_path", delimitedDataFile.parent_relative_path, Field.Store.YES)
        doc.add(parent_relative_path_field)
        val filename_field = new TextField("_filename", delimitedDataFile.file.name, Field.Store.YES)
        doc.add(filename_field)
        val hash_digest_field = new TextField("_md5_hash_prefix", delimitedDataFile.hash, Field.Store.YES)
        doc.add(hash_digest_field)
        val line_number_field = new LongPoint("_line_number", line_number)
        doc.add(line_number_field)
        val line_number_str_field = new TextField("_line_number_str", line_number.toString, Field.Store.YES)
        doc.add(line_number_str_field)
        // add rows of column_name:column_value pairs
        lineNameValueMap.foreach {
          named_pairs => {
            // named_pairs._1 : column name
            // named_pairs._2 : column value
            // @TODO reuse Document and Field instance to reduce GC (@SEE https://wiki.apache.org/lucene-java/ImproveIndexingSpeed)
            val field = new TextField(named_pairs._1, named_pairs._2, Field.Store.YES)
            doc.add(field)
          }
        }
        indexWriter.addDocument(doc)
      }
    )

    indexWriter.commit()
    indexWriter.close()
    logger.info(s"*****Completed: ${delimitedDataFile.file.pathAsString} ($line_number lines)")
  }
}

case class LuceneIndexService(indexDataDirectory : File) extends LuceneIndexServiceTrait

object LuceneIndexService extends Logging {

  def writeToDoc[DelimitedDataSource <: DelimitedDataFileTrait](delimitedDataFile: DelimitedDataSource, indexWriter: IndexWriter): Unit = {
    logger.info("*****Indexing: " + delimitedDataFile.file.pathAsString)
    var line_number: Long = 1
    delimitedDataFile.nameValueMapIterator.foreach(
      lineNameValueMap => {
        line_number = line_number + 1
        var doc = new Document()
        val parent_relative_path_field = new TextField("_relative_parent_path", delimitedDataFile.parent_relative_path, Field.Store.YES)
        doc.add(parent_relative_path_field)
        val filename_field = new TextField("_filename", delimitedDataFile.file.name, Field.Store.YES)
        doc.add(filename_field)
        val hash_digest_field = new TextField("_md5_hash_prefix", delimitedDataFile.hash, Field.Store.YES)
        doc.add(hash_digest_field)
        val line_number_field = new LongPoint("_line_number", line_number)
        doc.add(line_number_field)
        val line_number_str_field = new TextField("_line_number_str", line_number.toString, Field.Store.YES)
        doc.add(line_number_str_field)

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
    logger.info(s"*****Completed: ${delimitedDataFile.file.pathAsString} ($line_number lines)")
  }
}

