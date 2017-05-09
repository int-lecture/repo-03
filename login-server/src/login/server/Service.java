package login.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	 * The user base. Keys are tokens.
	 */
	private static Map<String, User> users = new HashMap<>();

	private static Map<String, String> testLoginData = new HashMap<>();
	private static Map<String, String> testValidateData = new HashMap<>();

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
		testLoginData.put("bob@web.de", "halloIchbinBob");
		String user = "";
		String password = "";
		try {
			JSONObject obj = new JSONObject(jsonString);
			password = obj.getString("password");
			System.out.println("password: " + password);
			user = obj.getString("user");
			System.out.println("user: " + user);

		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("Problem beim jsonString extrahieren");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		if (testLoginData.get(user).equals(password)) {
			JSONObject obj = new JSONObject();
			SimpleDateFormat sdf = new SimpleDateFormat(Service.ISO8601);
			try {
				obj.put("expire-date", sdf.format(new Date()));
				// TODO: Token generator(wenn wir das überhaupt machen müssen
				// und nicht von nem anderen Server kommt
				obj.put("token", "YXNkaCBhc2R6YWllIHVqa2RzaCBzYWlka");
			} catch (JSONException e) {
				System.out.println("Problem beim jasonobjekt füllen");
				e.printStackTrace();
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
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
		testValidateData.put("bob", "YXNkaCBhc2R6YWllIHVqa2RzaCBzYWlka");
		String token = "";
		String pseudonym = "";
		try {
			JSONObject obj = new JSONObject(jsonString);
			token = obj.getString("token");
			pseudonym = obj.getString("pseudonym");
		} catch (JSONException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
		if (token == testValidateData.get(pseudonym)) {
			JSONObject obj = new JSONObject();
			SimpleDateFormat sdf = new SimpleDateFormat(Service.ISO8601);
			try {
				obj.put("success", "true");
				obj.put("expire-date", sdf.format(new Date()));

			} catch (JSONException e) {
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}
			return Response.status(Response.Status.OK).entity(obj.toString()).build();

		}
		return Response.status(Response.Status.UNAUTHORIZED).build();

	}
}
