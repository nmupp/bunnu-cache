package com.bunnu.cache.actors

import akka.actor.{ActorSystem, Props, Status}
import akka.pattern.ask
import akka.util.Timeout
import com.bunnu.cache.messages.{Delete, GetRequest, SetIfNotExists, SetRequest}
import org.scalatest.{BeforeAndAfterEach, FunSpecLike, Matchers}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by nmupp on 12/16/16.
  */
class BunnuDBActorSpec extends FunSpecLike with Matchers with BeforeAndAfterEach {

  implicit val actorSystem = ActorSystem("TestActor")
  implicit val timeOut = Timeout(5 seconds)
  implicit val context = ExecutionContext.global
  val actor = actorSystem.actorOf(Props(classOf[BunnuDBActor]))

  describe("Bunnudb") {
    describe("given set request") {
      it("should cache the value successfully") {
        val future = actor ? SetRequest("narsi", "value")
        future.onSuccess {
          case result => assert(result.equals(Status.Success))
        }
        Thread.sleep(1000)
      }

      it("should get the value from cache") {
        val future = actor ? SetRequest("test","value")
        future
          .flatMap(_ => actor ? GetRequest("test"))
          .onSuccess {
            case result => assert(result.equals("value"))
          }
        Thread.sleep(1000)
      }

      it("should be failed status on fetching the key which is absent") {
        val future = actor ? GetRequest("test")
        future.onSuccess {
          case result:Status.Failure => result.cause should equal("key not found")
        }
        Thread.sleep(1000)
      }

      it("should get unknown message status") {
        val future = actor ? "unknown message"
        future.onSuccess {
          case result:Status.Failure => result.cause should equal("Unknown message")
        }
        Thread.sleep(1000)
      }

      it("should cache the value if not exists") {
        val future = actor ? SetIfNotExists("test", "value")
        future.
          flatMap {
            case v:Object => actor ? SetIfNotExists("test",v)
          }
          .onSuccess {
            case result => result should equal("value")
          }
        Thread.sleep(1000)
      }

      it("should uncache the value") {
        val future = actor ? SetRequest("test","value")
        future
          .flatMap(v => actor ? Delete("test"))
          .onSuccess {
            case result => result should equal("value")
          }
        Thread.sleep(1000)
      }

      ignore("recover when exception") {
        (actor ? SetRequest(null,null)).recoverWith {
          case ex: Exception =>
            println(s"the exception is ==> ${ex.getMessage}")
            actor ? GetRequest("success")
        }.onSuccess {
          case x: Status.Failure => x.cause should equal("Key not found")
        }
        Thread.sleep(1000)
      }
    }
  }
}
