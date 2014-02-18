package tutorial

import geotrellis._
import geotrellis.process.{Error, Complete}
import geotrellis.render.{ColorRamps, Color}
import geotrellis.source.RasterSource
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.{Response, Context}
import javax.ws.rs.{GET, PathParam, Path}


@Path("/combine")
class CombineRaster {
  @GET
  def get(@Context req:HttpServletRequest) = {


    val incomePerCapRaster = RasterSource("SBN_inc_percap")
    val farmMarketRaster = RasterSource("SBN_farm_mkt")
    val farmMarketMaskRaster = farmMarketRaster.localMap{ x => if (x > 1) 1 else NODATA }

    val result = incomePerCapRaster *  farmMarketMaskRaster


    val percapStats = incomePerCapRaster.histogram().get.generateStatistics()
    val maskedStats = result.histogram().get.generateStatistics()
    println(s"Result masked average: ${percapStats}")
    println(s"Result original average: ${maskedStats}")

    //renderPng will generate the histogram from the raster and use colors correctly
    result.renderPng(ColorRamps.BlueToRed).run match {
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
