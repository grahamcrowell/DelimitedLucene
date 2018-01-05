package com.github.grahamcrowell.LuceneDelimited

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}
import better.files.File
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

class SyncManagerTest extends FunSpec with Matchers with BeforeAndAfter {

  val root_folder = File("/Users/gcrowell/Documents/git/DelimitedLucene/test_data/indexing_tests/mock_tenant_root")
  var fileSystemSyncStateReader: FileSystemSyncStateReader = _
  var syncStateDiff: SyncStateDiff = _
  var syncManager: SyncManager = _

  describe("SyncManagerTest") {
    before {
      fileSystemSyncStateReader = FileSystemSyncStateReader(root_folder)
      syncStateDiff = SyncStateDiffImpl(fileSystemSyncStateReader, fileSystemSyncStateReader)
      syncManager = SyncManagerImpl(syncStateDiff)
    }
    it("should generateSourceTasks") {
      implicit val actorSystem: ActorSystem = ActorSystem("OneWaySyncManager")
      implicit val materializer = ActorMaterializer()
      val syncState = fileSystemSyncStateReader.readSyncState

      //      syncState.foreach(println(_))
      //      syncStateDiff.source_state.readSyncState.foreach(println(_))
      val func: () => Iterator[SyncElement] = () => {
        syncStateDiff.sourceNotDestinationSyncElements.iterator
      }
      //      syncStateDiff.sourceNotDestinationSyncElements.iterator.foreach(println(_))
      //      func().foreach(println(_))
      val src: Source[SyncElement, NotUsed] = Source.fromIterator(func)
      val flow = src.via(Flow[SyncElement]
        //          .map(syncElement=>syncElement.filename)
        .map(syncElement => SyncElementHelper.toFile(syncElement))
//        .filter(file => file.isRegularFile)
//        .map(DelimiterSniffer.sniffFile)
//        .filter(_.isDefined)
        //        .map(delimitedFile => s"${delimitedFile.get.columnNames.mkString(",")}")
      )
      flow.runForeach(println(_))
      //      println(syncState.head.filename)
      //      println(syncState(2).filename)
      //      val y =
      //      syncManager.generateSourceTasks.filter(x => true).foreach(println(_))
      //      println(y.)
    }

  }
}
