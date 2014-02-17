package tutorial

import javax.servlet.http.HttpServletRequest
import javax.ws.rs.{GET, Path, PathParam}
import javax.ws.rs.core.{Response,Context}

import geotrellis._
import geotrellis.data.ColorRamps._

@Path("/simpleDraw")
class SimpleDrawRaster {
  @GET
  def get(@Context req:HttpServletRequest) = {
    val rasterOp:Op[Raster] = 
        io.LoadRaster("philly_inc_percap")
    val pngOp:Op[Array[Byte]] = 
        io.SimpleRenderPng(rasterOp, BlueToRed)

    try {
      val img:Array[Byte] = Main.server.run(pngOp)
      Response.ok(img)
              .`type`("image/png")
              .build()
    } catch {
      case e:Throwable => 
        Response.ok(s"Error: $e")
                .`type`("text/plain")
                .build()
    }
  }
}