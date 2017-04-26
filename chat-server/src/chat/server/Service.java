package chat.server;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Provides a basic REST chat server.
 */
@Path("")
public class Service {

	/** String for date parsing in ISO 8601 format. */
	public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	/**
	 * The user base.
	 */
	private static HashMap<String, User> users = new HashMap<>();

	/**
	 * Receives new message from the user.
	 * @param json A JSON object containing the fields to,from,date and text.
	 * @return If successful returns 204(Created) and a JSON object containing date and sequenceNumber of the Message.
	 */
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
				try {
					return Response.status(Response.Status.CREATED).entity(msg.toJson(true).toString()).build();
				} catch (JSONException e) {
					e.printStackTrace();
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.err.println("TODO : Return 4xx error.");
			System.err.println(e.getErrorOffset());
			e.printStackTrace();
		}

		return Response.status(Response.Status.BAD_REQUEST).entity("Message was not correctly formatted").build();
	}

	/**
	 * Queries new messages for the user.
	 * @param userID The user's name.
	 * @return If successful returns 200(OK) and a JSON array of new messages. If no new messages are available
	 * returns 204(No Content).
	 */
	@GET
	@Path("/messages/{userid}")
	public Response getMessages(@PathParam("userid") String userID) {
		return getMessages(userID, 0);
	}

	/**
	 * Queries new messages for the user and removes all messages older than the given sequence number.
	 * @param userID The user's name.
	 * @param sequenceNumber The starting sequenceNumber.
	 * @return If successful returns 200(OK) and a JSON array of new messages. If no new messages are available
	 * returns 204(No Content).
	 */
	@GET
	@Path("/messages/{userid}/{sequenceNumber}")
	public Response getMessages(@PathParam("userid") String userID, @PathParam("sequenceNumber") int sequenceNumber) {
		if (Service.users.containsKey(userID)) {
			JSONArray jsonMsgs = new JSONArray();
			User user = Service.users.get(userID);
			List<Message> newMsgs = user.receiveMessages(sequenceNumber);
			if (newMsgs.isEmpty()) {
				return Response.status(Response.Status.NO_CONTENT).entity("No new messages").build();
			} else {
				for (Message msg : newMsgs) {
					try {
						jsonMsgs.put(msg.toJson(false));
					} catch (JSONException e) {
						e.printStackTrace();
						return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
					}
				}

				try {
					return Response.status(Response.Status.OK).entity(jsonMsgs.toString(4)).build();
				} catch (Exception e) {
					e.printStackTrace();
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}
			}
		} else {
			return Response.status(Response.Status.BAD_REQUEST).entity("User not found.").build();
		}
	}
}