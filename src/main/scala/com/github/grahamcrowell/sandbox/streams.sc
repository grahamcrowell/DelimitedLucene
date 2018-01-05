import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.grahamcrowell.LuceneDelimited.{SyncManager, SyncStateDiff}
import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

object Service {
  implicit val actorSystem: ActorSystem = ActorSystem("OneWaySyncManager")
  implicit val materializer = ActorMaterializer()

  def get(root_path: String): Unit = {

  }
}

val service = Service
