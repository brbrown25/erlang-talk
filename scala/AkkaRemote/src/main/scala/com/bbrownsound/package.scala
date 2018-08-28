package com

package object bbrownsound {
  sealed trait RemoteMessage
  case object START extends RemoteMessage
  case object WHOAMI extends RemoteMessage
  case class Greeting(msg: String) extends RemoteMessage
}
