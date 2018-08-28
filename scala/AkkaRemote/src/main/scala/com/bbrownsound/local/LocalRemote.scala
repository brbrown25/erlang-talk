package com.bbrownsound.local

import akka.actor._
import com.bbrownsound._
import com.bbrownsound.machine.MachineInfo
import com.typesafe.config._

//https://doc.akka.io/docs/akka/2.4.11.2/scala/remoting.html
object LocalRemote extends App {
  val cfg = ConfigFactory.load("application-local.conf")
  implicit val system = ActorSystem("LocalSystem", cfg)
  val localActor = system.actorOf(Props(new LocalActor(cfg)), name = "LocalActor")
  localActor ! START
}

class LocalActor(config: Config) extends Actor {
  var counter = 0
  val remoteCfg = config.getConfig("akka.remote")
  val path = "akka.tcp://" +
    s"${remoteCfg.getString("user")}@" +
    s"${remoteCfg.getString("addr")}:${remoteCfg.getInt("port")}" +
    s"${remoteCfg.getString("path")}"
  val remote = context.actorSelection(path)

  def receive = {
    case START =>
      remote ! Greeting("Hello from the LocalActor")
      remote ! WHOAMI
    case WHOAMI => MachineInfo.logProfile
    case Greeting(msg) if counter < 5 =>
      println(s"LocalActor received message: '$msg'")
      sender ! Greeting("Hello back to you")
      counter += 1
    case Greeting(_) =>
      context.system.terminate()
  }
}
