package com.bbrownsound.machine

import java.net.NetworkInterface
import scalaz._
import scalaz.Scalaz._
import scala.collection.convert.ToScalaImplicits

object MachineInfo {

  object NetworkUtils extends ToScalaImplicits {
    private val interfaces: List[(String, NetworkInterface)] = NetworkInterface
      .getNetworkInterfaces
      .toList
      .map(e => \/.fromTryCatchNonFatal(e.getDisplayName -> e))
      .collect { case \/-(s) => s }

    private val machineAddresses: List[(String, String)] = {
      interfaces
        .map(i => interfaceToMap(i._2))
        .collect { case \/-(s) => s }
    }

    def interfaceToMap(element: NetworkInterface): Throwable \/ (String, String) =
      \/.fromTryCatchNonFatal {
        val macAddress = element.getHardwareAddress
        val sb: StringBuilder = new StringBuilder(macAddress.length * 2)
        for (b <- macAddress) {
          sb.append("%02x".format(b & 0xFF))
        }
        (element.getDisplayName -> sb.toString())
      }

    def getMachineMacAddress: String = {
      machineAddresses.filter { case (i, _) =>
        i.equalsIgnoreCase("eth0") || i.equalsIgnoreCase("en0")
      }
      .map(_._2)
      .headOption | "000000000000"
    }

    def getPossibleMachineMacAddresses: Seq[String] = machineAddresses.map(_._2)

    def getIpAddresses: Map[String, String] = {
      interfaces
        .map(_._2)
        .flatMap { element =>
          element.getInetAddresses.flatMap { addr =>
            \/.fromTryCatchNonFatal {
              (element.getDisplayName -> addr.getHostAddress)
            }.toOption
          }
        }.toMap + ("public" -> "http://jonx.org/ip.php")
    }
  }

  case class NetworkProfile(
    primaryMacAddress: String = "",
    ipAddresses: Map[String, String] = Map("public" -> "http://jonx.org/ip.php"),
    macAddresses: Seq[String] = Seq(),
    carrier: Option[String] = None,
    imei: Option[String] = None,
    discoveredOn: Option[String] = None
  )

  case class JVMProfile(
    javaVersion: String = "",
    javaClasspath: String = "",
    javaEnv: String = ""
  )

  case class UnixProfile(
    uname: String = "",
    unixUser: String = "",
    cwd: String = ""
  )

  case class HardwareProfile(
    manufacturer: String = "",
    hardwareSerialNumber: String = "",
    mdmIdentifier: String = "",
    operatingSystem: Option[String] = None,
    operatingSystemVersion: Option[String] = None,
    firmwareVersion: Option[String] = None,
    voltageRating: Double
  )


  val jvmProfile = JVMProfile(util.Properties.javaVersion,util.Properties.javaClassPath,util.Properties.javaHome)
  import sys.process._
  val uname = "uname -a" !!
  val user = System.getProperty("user.name")
  val cwd = System.getProperty("user.dir")
  val unixProfile = UnixProfile(uname,user,cwd)
  val os = System.getProperty("os.name")
  val osVersion = System.getProperty("os.version")
  val osFirmware = scala.util.Try("/opt/vc/bin/vcgencmd version" !!).toOption

  val networkProfile = NetworkProfile(
    primaryMacAddress = NetworkUtils.getMachineMacAddress,
    ipAddresses = NetworkUtils.getIpAddresses,
    macAddresses = NetworkUtils.getPossibleMachineMacAddresses
  )

  def logProfile: Unit = {
    println(s"hardware profile: ${HardwareProfile("","","",Some(os),Some(osVersion),osFirmware,0.0)}")
    println(s"unix profile: ${unixProfile.uname} ${unixProfile.unixUser} ${unixProfile.cwd}")
    println(s"jvm profile: ${jvmProfile.javaVersion} ${jvmProfile.javaClasspath} ${jvmProfile.javaEnv}")
    println(s"networkProfile profile: ${networkProfile.primaryMacAddress} ${networkProfile.ipAddresses} ${networkProfile.macAddresses}")
  }
}
