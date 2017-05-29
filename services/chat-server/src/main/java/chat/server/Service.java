package chat.server;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Provides a basic REST chat server.
 */
@Path("")
public class Service {

	/** String for date parsing in ISO 8601 format. */
	public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZ";
	/**
	 * The user base.
	 */
	private static HashMap<String, User> users = new HashMap<>();

	/**
	 * Receives new message from the user.
	 *
	 * @param json
	 *            A JSON object containing the fields to,from,date and text.
	 * @return If successful returns 204(Created) and a JSON object containing
	 *         date and sequenceNumber of the Message.
	 */
	@PUT
	@Path("/send")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response send(String json) {
		System.out.println("Senden...");
		try {
			System.out.println(json);
			Message msg = Message.fromJson(json);
			System.out.println(msg);
			if (msg.to != null && msg.from != null && msg.date != null && msg.text != null && msg.token!=null) {
				User user;
				User thisUser;
				if (!users.containsKey(msg.to)) {
					user = new User(msg.to);
					users.put(msg.to, user);
				} else {
					user = users.get(msg.to);
				}
				if (!users.containsKey(msg.from)) {
					thisUser = new User(msg.from);
				users.put(msg.from, thisUser);
				} else {
					thisUser = users.get(msg.from);
				}

				System.out.println("Authentifizieren...");
				if (thisUser.authenticateUser(msg.token)) {
					msg = user.sendMessage(msg);
				} else {
					return Response.status(Response.Status.UNAUTHORIZED).build();
				}
				try {
					return Response.status(Response.Status.CREATED).header("Access-Control-Allow-Origin", "*").entity(msg.toJson(true).toString()).build();
				} catch (JSONException e) {
					e.printStackTrace();
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}
			}

		} catch (ParseException e) {
		}

		return Response.status(Response.Status.BAD_REQUEST).entity("Message was not correctly formatted").build();
	}

	/**
	 * Queries new messages for the user.
	 *
	 * @param userID
	 *            The user's name.
	 * @return If successful returns 200(OK) and a JSON array of new messages.
	 *         If no new messages are available returns 204(No Content).
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/messages/{userid}")
	public Response getMessages(@PathParam("userid") String userID, @Context HttpHeaders header) {
		return getMessages(userID, 0, header);
	}

	/**
	 * Queries new messages for the user and removes all messages older than the
	 * given sequence number.
	 *
	 * @param userID
	 *            The user's name.
	 * @param sequenceNumber
	 *            The starting sequenceNumber.
	 * @return If successful returns 200(OK) and a JSON array of new messages.
	 *         If no new messages are available returns 204(No Content).
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/messages/{userid}/{sequenceNumber}")
	public Response getMessages(@PathParam("userid") String userID, @PathParam("sequenceNumber") int sequenceNumber,
			@Context HttpHeaders header) {
		MultivaluedMap<String, String> map = header.getRequestHeaders();
		System.out.println(users);
		if (Service.users.containsKey(userID)) {
			JSONArray jsonMsgs = new JSONArray();
			User user = Service.users.get(userID);
			System.out.println(user.authenticateUser(map.get("Authorization").get(0)));
			if (user.authenticateUser(map.get("Authorization").get(0))) {
				List<Message> newMsgs = user.receiveMessages(sequenceNumber);
				if (newMsgs.isEmpty()) {
					return Response.status(Response.Status.NO_CONTENT).build();
				} else {
					for (Message msg : newMsgs) {
						try {
							jsonMsgs.put(msg.toJson(false));
						} catch (JSONException e) {
							e.printStackTrace();
							System.out.println("Json f�llen fehlgeschlagen");
							return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
						}
					}
					System.out.println(jsonMsgs);
					try {
						return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").entity(jsonMsgs.toString(4)).build();
					} catch (Exception e) {
						e.printStackTrace();
						return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
					}
				}
			} else {
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}
		} else {
		 return Response.status(Response.Status.BAD_REQUEST).entity("User not found.").build();
		}
	}


	@OPTIONS
	@Path("/send")
	public Response optionsReg() {
	    return Response.ok("")
	            .header("Access-Control-Allow-Origin", "*")
	            .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
	            .header("Access-Control-Allow-Credentials", "true")
	            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
	            .header("Access-Control-Max-Age", "1209600")
	            .build();
	}

	@OPTIONS
	@Path("/messages")
	public Response optionsProfile() {
	    return Response.ok("")
	            .header("Access-Control-Allow-Origin", "*")
	            .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
	            .header("Access-Control-Allow-Credentials", "true")
	            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
	            .header("Access-Control-Max-Age", "1209600")
	            .build();
	}

}