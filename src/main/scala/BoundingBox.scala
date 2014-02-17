package tutorial

import javax.servlet.http.HttpServletRequest
import javax.ws.rs.{GET, Path, PathParam}
import javax.ws.rs.core.{Response,Context}

import geotrellis._
import geotrellis.rest.op.string.ParseExtent
import geotrellis.raster.op.extent

@Path("/bbox/")
class BoundingBox {
    @GET
    @Path("/{extent1}/union/{extent2}")
    def get(@PathParam("extent1") s1:String,
            @PathParam("extent2") s2:String,
            @Context req:HttpServletRequest) = {
      // parse the given extents
      val e1:Op[Extent] = ParseExtent(s1)
      val e2:Op[Extent] = ParseExtent(s2)

      // combine the extents
      val op:Op[Extent] = extent.CombineExtents(e1, e2)

      // run the operation
      val message = try {
        val extent:Extent = Main.server.run(op)
        s"The Extent of the union is: $extent"
      } catch {
        case e:Throwable => s"Error: e"
      }

      Response.ok(message)
              .`type`("text/plain")
              .build()
    }
}