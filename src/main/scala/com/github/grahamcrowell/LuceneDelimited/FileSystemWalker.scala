package com.github.grahamcrowell.LuceneDelimited

import better.files.File

/**
  *
  */
object DataFilter {

  val dateRegex = """\d{8}"""

  /**
    * Curried method that tests a input file (under dataRoot)
    *
    * @param dataRoot Index/esldata/custdata data root
    * @param file     potential data file
    * @return true if file is a delimited file and so should be indexed
    */
  def timeStampFolder(dataRoot: File)(file: File): Boolean = {
    // no folders, symlinks etc
    file.isRegularFile &
      // must be a grand child of root (pattern is: <root>/<date stamp>/<data file name>
      (file.parent.parent.path == dataRoot.path) &
      // match containing folder name to a date stamp regex
      (file.parent.name matches dateRegex)
  }
}

trait DataRootWalker {
  val dataRoot: File
  private val dataFileFilter = DataFilter.timeStampFolder(dataRoot) _

  def delimitedFiles: Iterator[DelimitedFile] = {
    dataRoot.walk(2).filter(dataFileFilter)
      .flatMap(DelimiterSniffer.sniffFile)
  }
}

abstract class DataRootWalkerBase(dataRoot_ : File)
  extends DataRootWalker {
  override val dataRoot: File = dataRoot_
}

