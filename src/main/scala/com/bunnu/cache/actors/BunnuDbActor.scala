package com.bunnu.cache.actors

import akka.actor.{Actor, Status}
import akka.event.Logging
import com.bunnu.cache.messages.{Delete, GetRequest, SetIfNotExists, SetRequest}

import scala.collection.mutable

/**
  * Created by nmupp on 12/16/16.
  */
class BunnuDBActor extends Actor {

  val map = new mutable.HashMap[String, Object]()
  val log = Logging(context.system, this)

  override def receive: Receive = {
    case GetRequest(key) =>
      log.info("Received the get request")
      val value = map.get(key)
      value match {
        case Some(keyValue) => sender() ! keyValue
        case None => sender() ! Status.Failure(new Exception("Key not found"))
      }

    case SetRequest(key, value) =>
      log.info(s"The received message is $key and $value")
      map.put(key, value)
      sender() ! Status.Success

    case SetIfNotExists(key, value) =>
      log.info("Message received for key if not exists")
      map.get(key) match {
        case Some(keyValue) =>
          log.info(s"Value already cached for that key $key")
          sender() ! keyValue
        case None =>
          log.info(s"Value is not cached yet , so caching now")
          map.put(key, value)
          sender() ! value
      }

    case Delete(key) =>
      log.info(s"Message received to uncache the value for the $key")
      map.remove(key) match {
        case Some(value) => sender() ! value
        case None => Status.Success
      }

    case o =>
      log.info(s"received unknown messages: $o")
      sender() ! Status.Failure(new Exception("Unknown message"))
  }
}
