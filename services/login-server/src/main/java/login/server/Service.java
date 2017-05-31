package login.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.OPTIONS;
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
	private static SelectorThread threadSelector = null;

	/** String for date parsing in ISO 8601 format. */
	public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZ";

	private StorageProviderMongoDB spMDB = new StorageProviderMongoDB();

	public static void main(String[] args) {
		try {
			Config.init(args);
		} catch (Exception e) {
			System.out.println("Invalid launch arguments!");
			System.exit(-1);
		}
		startLoginServer(Config.getSettingValue(Config.baseURI));
	}

	public static void startLoginServer(String uri){
		final String baseUri = uri;
		final String paket = "login.server";
		final Map<String, String> initParams = new HashMap<String, String>();

		initParams.put("com.sun.jersey.config.property.packages", paket);
		System.out.println("Starte grizzly...");
		try {
			threadSelector = GrizzlyWebContainerFactory.create(baseUri, initParams);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.printf("Grizzly(loginServer) running at %s%n", baseUri);

	}
	public static void stopLoginServer(){
		//System.exit(0);
		threadSelector.stopEndpoint();
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
		String userName, password, pseudonym;
		boolean allowEmailLogin = Config.getSettingValue(Config.allowEmailLogin) == "true";
		try {
			JSONObject obj = new JSONObject(jsonString);
			password = obj.getString("password");
			userName = obj.getString("user");
			pseudonym = obj.optString("pseudonym");
			System.out.println("user: " + userName);

		} catch (JSONException e) {
			System.out.println("[/login] Failed to parse json request.");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		// Check in settings if a login with partial login data is allow.
		if (pseudonym == null && !allowEmailLogin) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		User user = spMDB.retrieveUser(userName, pseudonym);
		if (user != null && user.VerifyPassword(password)) {
			JSONObject obj = new JSONObject();
			user.GenerateToken();
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(Service.ISO8601);
				Calendar expireDate = user.GetTokenExpireDate();
				sdf.setTimeZone(expireDate.getTimeZone());
				obj.put("expire-date", sdf.format(expireDate.getTime()));
				obj.put("token", user.GetToken().toString());
				obj.put("pseudonym", user.pseudonym);
			} catch (JSONException e) {
				System.out.println("[/login] Error when building json response.");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			}
			SimpleDateFormat sdf = new SimpleDateFormat(Service.ISO8601);
			Calendar expireDate = user.GetTokenExpireDate();
			sdf.setTimeZone(expireDate.getTimeZone());
			spMDB.saveToken(user.GetToken(), sdf.format(expireDate.getTime()), user.pseudonym);
			return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").entity(obj.toString()).build();
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
		String token;
		String pseudonym;
		try {
			JSONObject obj = new JSONObject(jsonString);
			token = obj.getString("token");
			pseudonym = obj.getString("pseudonym");
		} catch (JSONException e) {
			System.out.println("[/auth] Failed to parse json request.");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		Date expireDate = spMDB.retrieveToken(pseudonym, token);
		if (expireDate != null) {
			Calendar cal = Calendar.getInstance();
			if (cal.getTime().before(expireDate)) {
				JSONObject obj = new JSONObject();
				try {
					obj.put("success", "true");
					obj.put("expire-date", expireDate);
					return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").entity(obj.toString()).build();

				} catch (JSONException e) {
					System.out.println("[/auth] Error when building json response.");
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}
			} else {
				// Token has expired
				spMDB.deleteToken(token);
			}
		}
		return Response.status(Response.Status.UNAUTHORIZED).build();

	}

	@OPTIONS
	@Path("/login")
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
	@Path("/auth")
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
