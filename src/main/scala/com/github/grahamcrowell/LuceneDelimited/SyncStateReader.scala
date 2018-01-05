package com.github.grahamcrowell.LuceneDelimited

import better.files.File

import scala.collection.immutable
//import scala.collection.parallel.immutable

trait SyncStateReader {
  val root_abspath: String

  def readSyncState: immutable.IndexedSeq[SyncElement]
}

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
  def timeStampFolderFilter(dataRoot: File)(file: File): Boolean = {
    // no folders, symlinks etc
    file.isRegularFile &
      // must be a grand child of root (pattern is: <root>/<date stamp>/<data file name>
      (file.parent.parent.path == dataRoot.path) &
      // match containing folder name to a date stamp regex
      (file.parent.name matches dateRegex)
  }
}

case class FileSystemSyncStateReader(root_folder: File)
  extends SyncStateReader {
  override val root_abspath: String = root_folder.pathAsString
  private val fileSystemSyncElementBuilder = FileSystemSyncElement.fileSystemSyncElementBuilder(root_folder) _
  private val dataFileFilter = DataFilter.timeStampFolderFilter(root_folder) _

  override def readSyncState: immutable.IndexedSeq[SyncElement] = {
    root_folder
      .walk(2)
      .filter(dataFileFilter)
      .map(fileSystemSyncElementBuilder)
      .toIndexedSeq
  }
}

object FileSystemSyncStateReader {
  def apply(root_abspath: String): FileSystemSyncStateReader = FileSystemSyncStateReader(File(root_abspath))
}

case class LuceneSyncStateReader(root_abspath: String)
  extends SyncStateReader {
  override def readSyncState: immutable.IndexedSeq[SyncElement] = ???
}

trait SyncStateDiff {
  val source_state: SyncStateReader
  val destination_state: SyncStateReader

  /** @todo implement diff
    */
  def sourceNotDestinationSyncElements: immutable.IndexedSeq[SyncElement] = {
    source_state.readSyncState
  }

  def destinationNotSourceSyncElements: immutable.IndexedSeq[SyncElement] = {
    ???
  }

  def syncedSyncElements: immutable.IndexedSeq[SyncElement] = {
    ???
  }
}

case class SyncStateDiffImpl(source_state: SyncStateReader, destination_state: SyncStateReader)
  extends SyncStateDiff