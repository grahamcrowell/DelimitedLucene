package com.github.grahamcrowell.LuceneDelimited

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}
import better.files.File
import org.apache.lucene.document.Document
import org.scalatest.{BeforeAndAfter, FunSpec, Matchers}

import scala.concurrent.Future

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
    it("compose Akka stream source, flow, and sink") {
      implicit val actorSystem: ActorSystem = ActorSystem("OneWaySyncManager")
      implicit val materializer: ActorMaterializer = ActorMaterializer()
      val process: Flow[SyncElement, Iterator[Document], NotUsed] = Flow[SyncElement]
        .map(SyncElementHelper.toFile)
        .alsoTo(Sink.foreach(file => println(file.name)))
        .map(DelimiterSniffer.sniffFile)
        .collect {
          case delimit if delimit.isDefined => delimit.get
        }
        .map(DocumentBuilderImpl)
        .map(_.documentIterator)

      val source: Source[SyncElement, NotUsed] = Source[SyncElement](syncStateDiff.sourceNotDestinationSyncElements)
      val idx: DocumentIndexerImpl = DocumentIndexerImpl(root_folder)
      val sink: Sink[Iterator[Document], Future[Done]] = Sink.foreach[Iterator[Document]](idx.indexDocumentIterator)
      val graph: RunnableGraph[NotUsed] = source.via(process).to(sink)
//      val graph: RunnableGraph[NotUsed] = source.via(process).to(Sink.foreach(x=>println("")))
      graph.run()(materializer)
      println(actorSystem.settings)
      1 should be (1)

    }

  }
}
