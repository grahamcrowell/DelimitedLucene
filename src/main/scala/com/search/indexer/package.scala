package com.search

import better.files.File

package object indexer {
  val esldata = File("/Users/gcrowell/workspace/esldata")
  val tenant_code = "WFF_m1f"
  val delimiters: Map[String, Char] = Map("pipe" -> '|', "tab" -> '\t', "comma" -> ',')
  def index_name : String = { tenant_code.toLowerCase}

}

