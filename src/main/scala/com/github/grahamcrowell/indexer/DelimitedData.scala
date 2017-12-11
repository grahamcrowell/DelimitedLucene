package com.github.grahamcrowell.indexer

import better.files.File
import org.apache.logging.log4j.scala.Logging
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.io.Source
import scala.util.Try

/**
  * Each tenant has one of these.
  * `/esldata/WFF_xxy0z/`
  */
trait TenantRootTrait {
  lazy val datedFolders: Iterator[DatedDataFolderTrait] = {
    file.children
      .filter(DatedDataFolder.isDatedDataFolder)
      .map(DatedDataFolder(_))
  }
  lazy val dataFiles: Iterator[DelimitedDataFileTrait] = {
    datedFolders.map(_.delimitedDataFiles).flatten
  }
  val file: File
}

case class TenantRoot(file: File) extends TenantRootTrait

trait DatedDataFolderTrait extends Logging {
  val file: File
  val delimitedDataFiles: Iterator[DelimitedDataFileTrait] = {
    logger.debug(s"delimitedDataFiles (${file.children.count(_ => true)})")
    file.children.filter((file: File) => file.exists && file.isRegularFile).filter((file: File) =>
      // filter out file if file.name matches any patterns in global list: source_data_file_ignore
      source_data_file_ignore.forall((ignore_pattern) => !file.name.matches(ignore_pattern))
    )
      .flatMap((dataFile: File) => DelimitedDataFile.toDelimitedDataFile(dataFile))
  }
}

case class DatedDataFolder(file: File) extends DatedDataFolderTrait with Logging

object DatedDataFolder {
  private val date_pattern = DateTimeFormat.forPattern("YYYYmmdd")

  def isDatedDataFolder(file: File): Boolean = {
    file.isDirectory && Try[DateTime](date_pattern.parseDateTime(file.name)).isSuccess
  }
}

trait DelimitedDataFileTrait {
  val file: File
  val hash: String = file.md5.toString.take(8)
  val parent_relative_path: String = file.parent.path.subpath(esldata.path.getNameCount, file.parent.path.getNameCount).toString
  val file_relative_path: String = file.path.subpath(esldata.path.getNameCount, file.path.getNameCount).toString
  val delimiter: Char
  val header: IndexedSeq[String]
  val nameValueMapIterator: Iterator[Map[String, String]]
}

case class DelimitedDataFile(file: File, delimiter: Char) extends DelimitedDataFileTrait with Logging {
  logger.debug(s"DelimitedDataFile (${file.pathAsString}: ${
    delimiter match {
      case ',' => "comma"
      case '|' => "pipe"
      case '\t' => "tab"
      case _ => ""
    }
  })")

  /**
    * Iterator over rows.  Each row is a Map[COLUMN_NAME, VALUE]
    */
  lazy val nameValueMapIterator: Iterator[Map[String, String]] = {
    lines.map {
      (line: String) => (header, line.split(delimiter)).zipped.toMap
    }
  }
  lazy private val lines: Iterator[String] = Source.fromFile(file.pathAsString).getLines()
  val header: IndexedSeq[String] = {
    logger.debug(s"header: ${lines.next().split(delimiter)}")
    lines.next().split(delimiter)
  }
}

object DelimitedDataFile extends Logging {
  def inferDelimiter(file: File): Option[String] = {
    logger.info(s"${this.getClass.getName}: ${file.path}")
    val file_rdr = Source.fromFile(file.pathAsString)
    val sample = file_rdr.getLines()
    val header_line = sample.next()
    val sample_lines = sample.take(11).toList
    file_rdr.close()
    println(header_line)
//    sample_lines.foreach(println)
//    println("please fucking work")
    val scores = delimiters.map {
      delimiter_pair => {
        val delimiter_name = delimiter_pair._1
        val delimiter_value = delimiter_pair._2
        println(delimiter_name)
        val header_count = header_line.split(delimiter_value).length.toDouble
//        println(s"header field count($delimiter_name) = $header_count")
        val field_counts = sample_lines.map {
          sample_line => sample_line.split(delimiter_value).length.toDouble
        } :+ header_count
        val average = field_counts.sum / field_counts.length
        val variance = field_counts.map {
          sample_count => Math.pow(sample_count - average,2)
        }.sum / field_counts.length
        println(average, variance)
        (delimiter_name, average, variance)
      }
    }.filter(_._2 != 1.0)

    Option("")
  }

  def toDelimitedDataFile(file: File): Option[DelimitedDataFile] = {
    logger.info(s"${file.getClass.getName}: ${file.path}")
    val file_rdr = Source.fromFile(file.pathAsString)
    val sample = file_rdr.getLines()
    val header_line = sample.next()
    val sample_lines = sample.take(10)
    file_rdr.close()

    val delimiter: Option[Char] = {
      val delimiter_sniff = delimiters.values.map((delimiter) => {
        val header_count = header_line.count(_ == delimiter)
        val line_counts = sample_lines.map((line: String) => line.count(_ == delimiter))
        (delimiter, (header_count, line_counts))
      }).filter(sep => sep._2._1 > 0).toList.sortBy(sep => sep._2._1).map(sep => sep._1)
      Some(delimiter_sniff.head)
    }
    delimiter.map(DelimitedDataFile(file, _))
  }

  def toDelimitedDataFile2(file: File): Option[DelimitedDataFile] = {
    Some(DelimitedDataFile(file, ','))
  }

}


