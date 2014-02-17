package tutorial

import geotrellis.process.{Server,Catalog}

/** WebRunner now lives in geotrellis-jetty project
*   changed from: "import geotrellis.rest.WebRunner"
*/
import geotrellis.jetty.WebRunner

object Main {
/** Geotrellis now has an implicit server object, this is not needed
  * Q: Where does it get the data from exactly? */
//  val server = Server("tutorial-server",
//                       Catalog.fromPath("data/catalog.json"))
	
  def main(args: Array[String]) = WebRunner.run()
}
