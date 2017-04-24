package chat_server;

import javax.ws.rs.Consumes;

//import java.awt.PageAttributes.MediaType;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("")
public class Service {

    /** String for date parsing in ISO 8601 format. */
    public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZ";

	@PUT
	@Path("/send")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public String send(String msg) {
		System.out.println(msg);

		return msg;
	}

}
