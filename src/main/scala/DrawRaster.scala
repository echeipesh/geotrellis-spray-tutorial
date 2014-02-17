package tutorial

import javax.servlet.http.HttpServletRequest
import javax.ws.rs.{GET, Path, PathParam}
import javax.ws.rs.core.{Response,Context}

import geotrellis._
import geotrellis.statistics.op._
import geotrellis.rest.op.string.{SplitOnComma,ParseColor,
                                  ParseInt}

@Path("/draw/")
class DrawRaster {
  @GET
  @Path("/palette/{palette}/shades/{shades}")
  def get(@PathParam("palette") palette:String,
          @PathParam("shades") shades:String,
          @Context req:HttpServletRequest) = {

    // load the raster
    val rasterOp = io.LoadRaster("philly_inc_percap")

    // find the colors to use
    val paletteOp =
      logic.ForEach(SplitOnComma(palette))(ParseColor(_))
    val numOp = ParseInt(shades)
    val colorsOp = stat.GetColorsFromPalette(paletteOp, numOp)

    // find the appropriate quantile class breaks to use
    val histogramOp = stat.GetHistogram(rasterOp)
    val breaksOp = stat.GetColorBreaks(histogramOp, colorsOp)

    // render the png
    val pngOp = 
      io.RenderPng(rasterOp, breaksOp, histogramOp, 0)

    // run the operation
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