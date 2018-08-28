package com.bbrownsound.remote

import akka.actor._
import com.bbrownsound._
import com.bbrownsound.machine.MachineInfo
import com.typesafe.config.ConfigFactory

object HelloRemote extends App {
  val cfg = ConfigFactory.load("application-remote.conf")
  val system = ActorSystem("HelloRemoteSystem", cfg)
  val remoteActor = system.actorOf(Props[RemoteActor], name = "RemoteActor")
  remoteActor ! Greeting("The RemoteActor is ALIVE!")
}

class RemoteActor extends Actor {
  def receive = {
    case WHOAMI =>
      println("remote machine whoami?")
      MachineInfo.logProfile
      sender ! WHOAMI
    case Greeting(msg) =>
      println(s"RemoteActor received message '${msg}'")
      sender ! Greeting(s"Hello from the RemoteActor @ ${System.currentTimeMillis()}")
  }
}
