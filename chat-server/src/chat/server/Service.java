package chat.server;

import java.text.ParseException;
import java.util.HashMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("")
public class Service {

	/** String for date parsing in ISO 8601 format. */
	public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	/**
	 * The user base.
	 */
	private static HashMap<String, User> users = new HashMap<>();

	@PUT
	@Path("/send")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public Response send(String json) {
		try {
			Message msg = Message.fromJson(json);
			if (msg.to != null && msg.from != null && msg.date != null && msg.text != null) {
				User user;
				if (!users.containsKey(msg.to)) {
					user = new User(msg.to);
					users.put(msg.to, user);
				} else {
					user = users.get(msg.to);
				}

				msg = user.sendMessage(msg);
				return Response.status(Response.Status.CREATED).entity(msg.toResponse()).build();
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.err.println("TODO : Return 4xx error.");
			System.err.println(e.getErrorOffset());
			e.printStackTrace();
		}

		return Response.status(Response.Status.BAD_REQUEST).entity("Message was not correctly formatted").build();
	}

	// public Response receive(){
	// }
}