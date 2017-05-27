package register.server;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

@Path("/")
public class Service {

	public static IStorageProvider storageProvider;
	public static String LoginServerURL = "http://localhost:5001";
	public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ssZ";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static SelectorThread startRegistrationServer(String uri) {
		final String baseUri = uri;
		final String paket = "login.server";
		final Map<String, String> initParams = new HashMap<String, String>();
		SelectorThread threadSelector = null;

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
		System.out.printf("Grizzly(registerServer) runnig at %s%n", baseUri);
		return threadSelector;
	}

	public static void stopLoginServer(SelectorThread threadSelector){
		threadSelector.stopEndpoint();
	}

	@PUT
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response RegisterNewUser(String jsonString) {
		String pseudonym = "";
		String email = "";
		String password = "";
		try {
			JSONObject obj = new JSONObject(jsonString);
			password = obj.getString("password");
			email = obj.getString("user");
			pseudonym = obj.getString("pseudonym");

		} catch (JSONException e) {
			System.out.println("(/register) User send invalid json data.");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		User user = new User(pseudonym, password, email);
		if(storageProvider.userExists(pseudonym, email)) {
			return Response.status(418).build();
		}

		if(storageProvider.createNewUser(user)) {
			JSONObject obj = new JSONObject();
			obj.append("success", "true");
			System.out.printf("(/register) Added new user %s. \n", email);
			return Response.status(Response.Status.OK).entity(obj.toString()).build();
		} else {
			System.out.println("(/register) Registration failed.");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	@POST
	@Path("/profile")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUserProfile(String jsonString) {
		String token = "";
		String pseudonym = "";
		try {
			JSONObject obj = new JSONObject(jsonString);
			token = obj.getString("token");
			pseudonym = obj.getString("getownprofile");

		} catch (JSONException e) {
			System.out.println("(/profile) User send invalid json data.");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		if(VerifyToken( pseudonym,  token)){
			User user = storageProvider.getUserProfile(pseudonym);
			JSONObject obj = new JSONObject();
			obj.append("name", user.getPseudonym());
			obj.append("email", user.getEmail());
			JSONArray contacts = new JSONArray(user.getContacts());
			obj.append("contact", contacts);

			return Response.status(Response.Status.OK).entity(obj.toString()).build();
		} else {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
	}

	private boolean VerifyToken(String pseudonym,String token) {
		try
		{
		JSONObject obj = new JSONObject();
		try {
			obj.put("token", token);
			obj.put("pseudonym", pseudonym);
			System.out.println("Authentifiziere "+pseudonym+"  "+ token);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// TODO : Maybe use one static client?
		Client webClient = new Client();
		String response = webClient.resource(LoginServerURL + "/auth")
		.accept(MediaType.APPLICATION_JSON)
		.type(MediaType.APPLICATION_JSON)
		.post(String.class, obj.toString());
		webClient.destroy();

		SimpleDateFormat sdf = new SimpleDateFormat(ISO8601);
			JSONObject jo = new JSONObject(response);
			if (jo.getString("success").equals("true")) {
				try {
					Date expireDate = sdf.parse(jo.getString("expire-date"));
					if(expireDate.before(new Date()))
					{
						return true;
					}
				} catch (JSONException | ParseException e) {
					System.out.printf("Could not verify user %s. Failed to parse auth json. \n",pseudonym);
					return false;
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}

		return false;
	}
}
