package com.github.grahamcrowell.LuceneDelimited

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

import scala.concurrent.Future

trait SyncManager {
  val syncStateDiff: SyncStateDiff
  implicit val actorSystem: ActorSystem = ActorSystem("OneWaySyncManager")
  implicit val materializer = ActorMaterializer()
  def generateSourceTasks: Future[Done] = {
    val source: Source[SyncElement, NotUsed] = Source[SyncElement](syncStateDiff.sourceNotDestinationSyncElements)
    val func: () => Iterator[SyncElement] = () => {
      syncStateDiff.sourceNotDestinationSyncElements.iterator
    }
    val src = Source.fromIterator[SyncElement](func)

    val flow = src.via(Flow[SyncElement]
      .map(syncElement => SyncElementHelper.toFile(syncElement))
      .map(DelimiterSniffer.sniffFile)
      .filter(_.isDefined)
      .map(delimitedFile => s"${delimitedFile.get.columnNames.mkString(",")}")
    )
    val sink = Sink.foreach[String](println(_))
    val runnableGraph = flow.runForeach(println(_))
    runnableGraph

  }

  def generateDestinationTasks: Unit = ???
}

case class SyncManagerImpl(syncStateDiff: SyncStateDiff)
  extends SyncManager

