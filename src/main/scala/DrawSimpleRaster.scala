package tutorial

import javax.servlet.http.HttpServletRequest
import javax.ws.rs.{GET, Path, PathParam}
import javax.ws.rs.core.{Response,Context}

import geotrellis._
import geotrellis.source.RasterSource
import geotrellis.render.ColorRamps
import geotrellis.process.{Complete, Error}

@Path("/simpleDraw")
class SimpleDrawRaster {
  @GET
  def get(@Context req:HttpServletRequest) = {

    val raster = RasterSource("philly_inc_percap")

    raster.renderPng(ColorRamps.BlueToRed).run match {
      case Complete(img, hist) =>
        Response.ok(img)
          .`type`("image/png")
          .build()

      case Error(msg, trace) =>
        Response.ok(s"Error: $msg")
          .`type`("text/plain")
          .build()

    }
  }
}