package com.bunnu.cache.actors

import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import com.bunnu.cache.messages.SetRequest
import org.scalatest.{BeforeAndAfterEach, FunSpecLike, Matchers}

/**
  * Created by nmupp on 12/16/16.
  */
class BunnuDBActorSpec extends FunSpecLike with Matchers with BeforeAndAfterEach {

    implicit val actorSystem = ActorSystem()

    describe("Bunnudb") {
      describe("given set request") {
        it("should place a key to the map") {
          val ref = TestActorRef(new BunnuDBActor)
          ref ! SetRequest("narsi","pranu")
          val actor = ref.underlyingActor
          actor.map.get("narsi") should equal (Some("pranu"))
        }

        it("should place the last value sent to the actor") {
          val ref = TestActorRef(new BunnuDBActor)
          ref ! SetRequest("rian", "Raju")
          ref ! SetRequest("rian", "Muppalla")
          val actor = ref.underlyingActor
          actor.map.get("rian") should equal (Some("Muppalla"))
        }
      }
    }
}