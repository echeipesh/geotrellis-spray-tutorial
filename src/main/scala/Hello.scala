package tutorial

import javax.servlet.http.HttpServletRequest
import javax.ws.rs.{GET, Path}
import javax.ws.rs.core.{Response,Context}

@Path("/")
class Hello {
  @GET
  def get(@Context req:HttpServletRequest) = {
    val message = "Hello World!"
    Response.ok(message)
            .`type`("text/plain")
            .build()
  }
}