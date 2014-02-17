package tutorial

import javax.servlet.http.HttpServletRequest
import javax.ws.rs.{GET, Path, PathParam}
import javax.ws.rs.core.{Response,Context}

import geotrellis._
import geotrellis.source.{RasterSource}
import geotrellis.render.{Color}
import geotrellis.process.{Error, Complete}



@Path("/draw/")
class DrawRaster {
  @GET
  @Path("/palette/{palette}/shades/{shades}")
  def get(@PathParam("palette") palette:String,
          @PathParam("shades") shades:String,
          @Context req:HttpServletRequest) = {

    val raster = RasterSource("philly_inc_percap")

    val num = shades.toInt
    val colors = palette.split(",").map(Color.parseColor(_))

    //renderPng will generate the histogram from the raster and use colors correctly
    raster.renderPng(colors, num).run match {
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