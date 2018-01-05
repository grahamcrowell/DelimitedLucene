package com.github.grahamcrowell.LuceneDelimited


import better.files.File

/**
  * lightweight abstraction of an indivisible object (eg. file in file system)
  *
  * lightweight because it only contains meta data
  * abstraction in that it is implemented differently depending on context (ie. differently in lucene vs file system)
  * indivisible in that it is indexed "all or none" (ie. cannot be partially indexed)
  */
trait SyncElement {
  val root_abspath: String
  val parent_relpath: String
  val filename: String
  val hash_digest: String
  val modified_timestamp: Long
}

object SyncElementHelper {
  implicit def toFile(syncElement: SyncElement): File = {
    File(syncElement.root_abspath) / syncElement.parent_relpath / syncElement.filename
  }
}

case class FileSystemSyncElement(root_abspath: String,
                                 parent_relpath: String,
                                 filename: String,
                                 modified_timestamp: Long)
  extends SyncElement {
  lazy override val hash_digest: String = file.md5
  private val file: File = File(root_abspath) / parent_relpath / filename
  assert(file.isRegularFile)
}

object FileSystemSyncElement {
  def fileSystemSyncElementBuilder(root_folder: File)(file: File): FileSystemSyncElement = {
    FileSystemSyncElement(root_folder, file)
  }

  def apply(root_folder: File, file: File): FileSystemSyncElement = {
    val filename = file.name
    val modified_timestamp = file.lastModifiedTime.toEpochMilli
    val parent_relpath = file
      .pathAsString
      .substring(root_folder.pathAsString.length + 1)
      .dropRight(file.name.length)
    FileSystemSyncElement(root_folder.pathAsString, parent_relpath, filename, modified_timestamp)
  }
}

case class LuceneSyncElement(root_abspath: String,
                             parent_relpath: String,
                             filename: String,
                             modified_timestamp: Long,
                             hash_digest: String)
  extends SyncElement
