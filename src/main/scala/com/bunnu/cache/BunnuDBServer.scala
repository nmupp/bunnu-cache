package com.bunnu.cache

import akka.actor.{ActorSystem, Props}
import com.bunnu.cache.actors.BunnuDBActor
import com.typesafe.config.ConfigFactory

/**
  * Created by nmupp on 12/28/16.
  */
object BunnuDBServer extends App {
  val config = ConfigFactory.load.getConfig("BunnuCacheService")
  val system = ActorSystem("BunnuDB", config)
  system.actorOf(Props(classOf[BunnuDBActor]), name = "BunnuDB")
}
