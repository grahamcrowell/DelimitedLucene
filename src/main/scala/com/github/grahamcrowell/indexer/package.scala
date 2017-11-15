package com.github.grahamcrowell

import better.files.File

package object indexer {
  val esldata = File("./esldata")
  val tenant_code = "WFF_cross_folder_subject"
  val delimiters: Map[String, Char] = Map("pipe" -> '|', "tab" -> '\t', "comma" -> ',')
  val source_data_file_ignore: Seq[String] = {
    Seq("\\.DS_Store")
  }

  def index_name: String = {
    tenant_code.toLowerCase
  }
}
