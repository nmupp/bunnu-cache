package com.bunnu.cache.actors

import akka.actor.Actor
import akka.event.Logging
import com.bunnu.cache.messages.SetRequest

import scala.collection.mutable

/**
  * Created by nmupp on 12/16/16.
  */
class BunnuDBActor extends Actor {

  val map = new mutable.HashMap[String, Object]()
  val log = Logging(context.system, this)

  override def receive: Receive = {
    case SetRequest(key, value) if key.isEmpty => log.info(s"Received empty key")
    case SetRequest(key, value) =>
      log.info(s"The received message is $key and $value")
      map.put(key, value)
      sender() ! value
    case o => log.info(s"received unknown messages: $o")
  }
}
