package com.bbrownsound.pong

import akka.actor._
import com.typesafe.config._

/*
* sbt "runMain com.bbrownsound.pong.Pong"
* sbt "runMain com.bbrownsound.pong.Ping"
* */
object Ping extends App {
  val cfg = ConfigFactory.load("application-ping.conf")
  implicit val system = ActorSystem("PingSystem", cfg)
  val localActor = system.actorOf(Props(new PingActor(cfg)), name = "PingActor")
  localActor ! StartMessage
}

class PingActor(config: Config) extends Actor with ActorLogging {
  val remoteCfg = config.getConfig("akka.remote")
  val path = "akka.tcp://" +
    s"${remoteCfg.getString("user")}@" +
    s"${remoteCfg.getString("addr")}:${remoteCfg.getInt("port")}" +
    s"${remoteCfg.getString("path")}"
  val remote = context.actorSelection(path)

  def receive = {
    case StartMessage =>
      log.info("ping start message")
      remote ! PongMessage(5)
    case PingMessage(n) if n == 0 =>
      log.info("ping finished")
      remote ! StopMessage
      // context.stop(self)
      context.system.terminate()
    case PingMessage(n) =>
      log.info(s"PingActor received message pong")
      sender ! PongMessage(n)
    case _ =>
      log.error("PingActor recieved a message I don't understand")
  }
}
