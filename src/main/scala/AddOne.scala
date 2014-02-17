package tutorial

import javax.servlet.http.HttpServletRequest
import javax.ws.rs.{GET, Path, PathParam}
import javax.ws.rs.core.{Response,Context}

import geotrellis.Op
import geotrellis.rest.op.string.ParseInt
import geotrellis.Implicits._

@Path("/addone/")
class AddOne {
    @GET
    @Path("/{x}")
    def get(@PathParam("x") s:String,
            @Context req:HttpServletRequest) = {
        // parse the given integer
        val opX:Op[Int] = ParseInt(s)

        // add one
        val opY:Op[Int] = opX + 1

        // run the operation
        val message:String = try {
            val y:Int = Main.server.run(opY)
            s"The result is: $y"
        } catch {
            case e:Throwable => s"Error: $e"
        }

        Response.ok(message)
              .`type`("text/plain")
              .build()
    }
}
