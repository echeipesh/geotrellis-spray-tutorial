package tutorial

import akka.actor._
import spray.routing.{ExceptionHandler, HttpService}
import geotrellis.source.{ValueSource, RasterSource}
import geotrellis.render.ColorRamps
import geotrellis.process.{Error, Complete}
import spray.http.MediaTypes
import spray.http.StatusCodes.InternalServerError
import spray.util.LoggingContext
import geotrellis._
import geotrellis.process.Error
import geotrellis.process.Complete
import geotrellis.statistics.Histogram

class GeoTrellisServiceActor extends GeoTrellisService with Actor {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  def receive = runRoute(rootRoute)
}


trait GeoTrellisService extends HttpService {

  implicit def myExceptionHandler(implicit log: LoggingContext) =
    ExceptionHandler {
      case e: Exception =>
        requestUri { uri =>
          complete(InternalServerError, s"Exception: ${e.getMessage}" )
        }
    }

  def rasterAsPng(name: String) = {
    val raster = RasterSource(name)

    raster.renderPng(ColorRamps.BlueToRed).run match {
      case Complete(img, hist) => img
      case Error(msg, trace) => throw new RuntimeException(msg)
    }
  }

  val rootRoute = {
    path("ping") {
      get { complete("pong!1") }
    } ~
    pathPrefix("raster" / Segment) { slug =>
      //Construct an object with instructions to fetch the raster
      val raster: RasterSource = RasterSource(slug)

      path("draw") {
        get {
          respondWithMediaType(MediaTypes.`image/png`) {
            complete {
              //Cunstruct an object that knows how to build a PNG once the Raster is loaded
              val png: ValueSource[Png] = raster.renderPng(ColorRamps.BlueToRed)

              //Perform the operations leading to this result
              png.run match {
                case Complete(img, hist) =>
                  img
                case Error(msg, trace) =>
                  throw new RuntimeException(msg)
              }
            }
          }
        }
      } ~
      path("mask") {
        get{
          parameter('cutoff.as[Int]) { cutoff =>
            respondWithMediaType(MediaTypes.`image/png`) {
              complete{
                val mask = raster.localMap{ x => if (x > cutoff) 1 else NODATA }
                mask.renderPng(ColorRamps.BlueToRed).get
              }
            }
          }
        }
      } ~
      path("stats") {
        get {
          respondWithMediaType(MediaTypes.`application/json`) {
            complete {
              val histogramSource: ValueSource[Histogram] = raster.histogram()
              //No processing has been done yet
              val histogram = histogramSource.get
              val stats = histogram.generateStatistics()
              s"{mean: ${stats.mean}, histogram: ${histogram.toJSON} }"
            }
          }
        }
      }
    } ~
    pathPrefix("analyze") {
      parameter('cutoff.as[Int]) { cutoff =>
        val incomePerCapRaster = RasterSource("SBN_inc_percap")
        val farmMarketRaster = RasterSource("SBN_farm_mkt")
        val farmMarketMaskRaster = farmMarketRaster.localMap{ x => if (x > cutoff) 1 else NODATA }

        val result = incomePerCapRaster *  farmMarketMaskRaster

        path("draw") {
          respondWithMediaType(MediaTypes.`image/png`) {
            complete {
              result.renderPng(ColorRamps.BlueToRed).get
            }
          }
        } ~
        path("stats") {
          get {
            respondWithMediaType(MediaTypes.`application/json`) {
              complete {
                val histogramSource: ValueSource[Histogram] = result.histogram()
                //No processing has been done yet
                val histogram = histogramSource.get
                val stats = histogram.generateStatistics()
                s"{mean: ${stats.mean}, histogram: ${histogram.toJSON} }"
              }
            }
          }
        }
      }
    }

  }

}
