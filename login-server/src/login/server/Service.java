package login.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

@Path("/")
public class Service {
	/** String for date parsing in ISO 8601 format. */
	public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZ";

	public static void main(String[] args) {
		// TODO : Remove before release!
		GenerateTestUsers();

		final String baseUri = "http://localhost:5001/";
		final String paket = "login.server";
		final Map<String, String> initParams = new HashMap<String, String>();

		initParams.put("com.sun.jersey.config.property.packages", paket);
		System.out.println("Starte grizzly...");
		SelectorThread threadSelector = null;
		try {
			threadSelector = GrizzlyWebContainerFactory.create(baseUri, initParams);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.printf("Grizzly(loginServer) läuft unter %s%n", baseUri);
		// Wait forever
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Grizzly wurde beendet");
		System.exit(0);
	}

	/**
	 * The user base. Keys are login identifiers i.e. email addresses.
	 */
	private static Map<String, User> users = new HashMap<>();

	/**
	 * Users with tokens currently in use. Tokens might be expired! Key are
	 * tokens
	 */
	private static Map<String, User> authedUsers = new HashMap<>();

	/**
	 * Only for debugging and testing!
	 */
	private static void GenerateTestUsers() {
		users.put("bob@web.de", new User("bob@web.de", "halloIchbinBob", "bob"));
		users.put("tom@web.de", new User("tom@web.de", "halloIchbinTom", "tom"));
	}

	/**
	 * Logs a user in if his credentials are valid.
	 *
	 * @param jsonString
	 *            A JSON object containing the fields user(email) and password.
	 * @return Returns a JSON object containing the fields token and
	 *         expire-date.
	 */
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response LoginUser(String jsonString) {
		String userName = "";
		String password = "";
		try {
			JSONObject obj = new JSONObject(jsonString);
			password = obj.getString("password");
			System.out.println("password: " + password);
			userName = obj.getString("user");
			System.out.println("user: " + userName);

		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("Problem beim jsonString extrahieren");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		if (users.containsKey(userName) && users.get(userName).VerifyPassword(password)) {
			JSONObject obj = new JSONObject();
			User user = users.get(userName);
			user.GenerateToken();
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(Service.ISO8601);
				Calendar expireDate = user.GetTokenExpireDate();
				sdf.setTimeZone(expireDate.getTimeZone());
				obj.put("expire-date", sdf.format(expireDate.getTime()));
				obj.put("token", user.GetToken());
			} catch (JSONException e) {
				System.out.println("Problem beim jasonobjekt füllen");
				e.printStackTrace();
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			}

			authedUsers.put(user.GetToken(),user);
			return Response.status(Response.Status.OK).entity(obj.toString()).build();
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	/**
	 * Validates a user token.
	 *
	 * @param jsonString
	 *            A JSON object containing the fields token and pseudonym.
	 * @return Returns a JSON object containing the fields expire-date and
	 *         success.
	 */
	@POST
	@Path("/auth")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response ValidateToken(String jsonString) {
		String token = "";
		String pseudonym = "";
		try {
			JSONObject obj = new JSONObject(jsonString);
			token = obj.getString("token");
			pseudonym = obj.getString("pseudonym");
			System.out.println(token);
			System.out.println(pseudonym);
		} catch (JSONException e) {
			System.out.println("Fehler beim extrahieren des jsonObject");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		if (authedUsers.containsKey(token) && authedUsers.get(token).pseudonym.equals(pseudonym)) {
			User user = authedUsers.get(token);
			if (user.VerifyToken(token)) {
				JSONObject obj = new JSONObject();
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(Service.ISO8601);
					Calendar expireDate = authedUsers.get(token).GetTokenExpireDate();
					sdf.setTimeZone(expireDate.getTimeZone());
					obj.put("success", "true");
					obj.put("expire-date", sdf.format(expireDate.getTime()));
					return Response.status(Response.Status.OK).entity(obj.toString()).build();

				} catch (JSONException e) {
					System.out.println("Fehler beim jsonObject füllen");
					return Response.status(Response.Status.UNAUTHORIZED).build();
				}
			} else {
				// Token has expired
				authedUsers.remove(token);
			}
		}
		return Response.status(Response.Status.UNAUTHORIZED).build();

	}
}
