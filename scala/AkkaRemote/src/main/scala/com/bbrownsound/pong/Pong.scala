package com.bbrownsound.pong

import akka.actor._
import com.typesafe.config._

/*
* sbt "runMain com.bbrownsound.pong.Pong"
* sbt "runMain com.bbrownsound.pong.Ping"
* */
object Pong extends App {
  val cfg = ConfigFactory.load("application-pong.conf")
  val system = ActorSystem("PongSystem", cfg)
  val remoteActor = system.actorOf(Props[PongActor], name = "PongActor")
  remoteActor ! StartMessage
}

class PongActor extends Actor with ActorLogging {
  def receive = {
    case StartMessage =>
      log.info("Pong Actor Started")
    case StopMessage =>
      log.info("Pong finished")
      // context.stop(self)
      context.system.terminate()
    case PongMessage(n) =>
      log.info(s"PongActor received  ping")
      sender ! PingMessage(n - 1)
    case _ =>
      log.error("PongActor recieved a message I don't understand")
  }
}
