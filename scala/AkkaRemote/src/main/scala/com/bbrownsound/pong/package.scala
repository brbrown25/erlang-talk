package com.bbrownsound

package object pong {
  sealed trait GameMessage
  case class PingMessage(num: Int) extends GameMessage
  case class PongMessage(num: Int) extends GameMessage
  case object StartMessage extends GameMessage
  case object StopMessage extends GameMessage
}