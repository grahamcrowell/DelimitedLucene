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

case class FileSystemSyncElement(root_abspath: String,
                                 parent_relpath: String,
                                 filename: String,
                                 modified_timestamp: Long)
  extends SyncElement {
  lazy override val hash_digest: String = file.md5
  private val file: File = File(root_abspath) / parent_relpath / filename
}

object FileSystemSyncElement {
  def apply(root_abspath: String, file: File): FileSystemSyncElement = {
    val root_folder = File(root_abspath)
    val filename = file.name
    val modified_timestamp = file.lastModifiedTime.toEpochMilli
    val parent_relpath = file.path.toAbsolutePath.toString.substring(root_folder.path.toAbsolutePath.toString.length, file.path.toAbsolutePath.toString.length)
    new FileSystemSyncElement(root_abspath, parent_relpath, filename, modified_timestamp)
  }

  def apply(root_folder: File, file: File): FileSystemSyncElement = {
    val filename = file.name
    val modified_timestamp = file.lastModifiedTime.toEpochMilli
    val parent_relpath = file.path.toAbsolutePath.toString.substring(root_folder.path.toAbsolutePath.toString.length, file.path.toAbsolutePath.toString.length)
    new FileSystemSyncElement(root_folder.path.toAbsolutePath.toString, parent_relpath, filename, modified_timestamp)
  }

  def fileSystemSyncElementBuilder(root_folder: File)(file: File): FileSystemSyncElement = {
    val filename = file.name
    val modified_timestamp = file.lastModifiedTime.toEpochMilli
    val parent_relpath = file.path.toAbsolutePath.toString.substring(root_folder.path.toAbsolutePath.toString.length, file.path.toAbsolutePath.toString.length)
    new FileSystemSyncElement(root_folder.path.toAbsolutePath.toString, parent_relpath, filename, modified_timestamp)
  }
}

case class LuceneSyncElement(root_abspath: String,
                             parent_relpath: String,
                             filename: String,
                             modified_timestamp: Long,
                             hash_digest: String)
  extends SyncElement

trait SyncStateReader {
  val root_abspath: String

  def readSyncState: IndexedSeq[SyncElement]
}

case class FileSystemSyncStateReader(root_abspath: String)
  extends SyncStateReader {
  private val root_folder: File = File(root_abspath)
  private val fileSystemSyncElementBuilder = FileSystemSyncElement.fileSystemSyncElementBuilder(root_folder) _

  override def readSyncState: IndexedSeq[SyncElement] = {
    root_folder.walk(2).map(fileSystemSyncElementBuilder).toIndexedSeq
  }
}

case class LuceneSyncStateReader(root_abspath: String)
  extends SyncStateReader {
  override def readSyncState: IndexedSeq[SyncElement] = {
    ???
  }
}

trait SyncStateDiff {
  val source_state: SyncStateReader
  val destination_state: SyncStateReader

  def sourceNotDestinationSyncElements: IndexedSeq[SyncElement]

  def destinationNotSourceSyncElements: IndexedSeq[SyncElement]

  def syncedSyncElements: IndexedSeq[SyncElement]
}

trait OneWaySyncManager {
  val syncStateDiff: SyncStateDiff

  def generateSourceTasks : Unit = ???
  def generateDestinationTasks : Unit = ???
}

