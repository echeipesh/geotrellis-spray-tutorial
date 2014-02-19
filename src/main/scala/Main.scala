package tutorial

import akka.actor.{ ActorRef, Props, Actor, ActorSystem }
import akka.routing.FromConfig

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http


object Main {
  def main(args: Array[String]): Unit = {
    val port = 8999 //config.getInt("geotrellis.port")
    val host = "localhost" //config.getString("geotrellis.hostname")

    // we need an ActorSystem to host our service
    implicit val system = ActorSystem()

    //create our service actor
    val service = system.actorOf(Props[GeoTrellisServiceActor], "geotrellis-service")

    //bind our actor to HTTP
    IO(Http) ! Http.Bind(service, interface = host, port = port)
  }
}
