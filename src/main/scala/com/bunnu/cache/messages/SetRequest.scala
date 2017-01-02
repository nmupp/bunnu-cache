package com.bunnu.cache.messages

/**
  * Created by nmupp on 12/16/16.
  */
case class SetRequest(key: String, value: Object)
case class GetRequest(key: String)
case class SetIfNotExists(key: String, value: Object)
case class Delete(key: String)
