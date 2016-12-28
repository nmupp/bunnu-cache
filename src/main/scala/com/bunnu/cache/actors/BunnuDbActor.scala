package com.bunnu.cache.actors

import akka.actor.{Actor, Status}
import akka.event.Logging
import com.bunnu.cache.messages.{GetRequest, SetRequest}

import scala.collection.mutable

/**
  * Created by nmupp on 12/16/16.
  */
class BunnuDBActor extends Actor {

  val map = new mutable.HashMap[String, Object]()
  val log = Logging(context.system, this)

  override def receive: Receive = {
    case GetRequest(key) =>
      val value = map.get(key)
      value match {
        case Some(keyValue) => sender() ! keyValue
        case None => sender() ! Status.Failure(new Exception("Key not found"))
      }
    case SetRequest(key, value) =>
      log.info(s"The received message is $key and $value")
      map.put(key, value)
      sender() ! Status.Success

    case o =>
      log.info(s"received unknown messages: $o")
      sender() ! Status.Failure(new Exception("Unknown message"))
  }
}
