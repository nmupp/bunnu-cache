package com.bunnu.cache.actors

import akka.actor.{ActorSystem, Props, Status}
import akka.pattern.ask
import akka.testkit.TestActorRef
import akka.util.Timeout
import com.bunnu.cache.messages.SetRequest
import org.scalatest.{BeforeAndAfterEach, FunSpecLike, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

/**
  * Created by nmupp on 12/16/16.
  */
class BunnuDBActorSpec extends FunSpecLike with Matchers with BeforeAndAfterEach {

  implicit val actorSystem = ActorSystem("MyActor")

  describe("Bunnudb") {
    describe("given set request") {
      it("success on caching the value") {
        implicit val timeOut = Timeout(5 seconds)
        implicit val context = ExecutionContext.global
        val actor = actorSystem.actorOf(Props(classOf[BunnuDBActor]))
        val future = actor ? SetRequest("narsi", "value")
        future.onSuccess {
          case Status.Success => println("This is Success")
        }
        Thread.sleep(1000)
      }

      ignore("should place a key to the map") {
        val ref = TestActorRef(new BunnuDBActor)
        ref ! SetRequest("narsi", "pranu")
        val actor = ref.underlyingActor
        actor.map.get("narsi") should equal(Some("pranu"))
      }

      ignore("should place the last value sent to the actor") {
        val ref = TestActorRef(new BunnuDBActor)
        ref ! SetRequest("rian", "Raju")
        ref ! SetRequest("rian", "Muppalla")
        val actor = ref.underlyingActor
        actor.map.get("rian") should equal(Some("Muppalla"))
      }

      ignore("should cache the value") {
        implicit val timeout = Timeout(5 seconds)
        val bunnuDBActor = actorSystem.actorOf(Props(classOf[BunnuDBActor]))
        val future = bunnuDBActor ? SetRequest("narsi", "pranu")
        val result = Await.result(future.mapTo[String], 1 second)
        assert(result == "pranu")
      }

      ignore("recover when exception") {
        val system = ActorSystem()
        implicit val timeOut = Timeout(5 seconds)
        implicit val executionContext = ExecutionContext.global
        val actor = system.actorOf(Props(classOf[BunnuDBActor]))
        val future = (actor ? SetRequest("","")).recoverWith {
          case ex: Exception =>
            println(s"the exception is ==> ${ex.getMessage}")
            actor ? SetRequest("success", "yes")
        }
        future.onSuccess {
          case x: String => println(s"Recovered with success")
        }
        Thread.sleep(10000)
      }
    }
  }
}
