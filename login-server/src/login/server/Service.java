package login.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

public class Service {
	public static void main(String[] args) {
		final String baseUri = "http://localhost:5000/";
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
		System.out.printf("Grizzly läuft unter %s%n", baseUri);
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
	private static Map<String,User> users = new HashMap<>();

	/**
	 * Logs a user in if his credentials are valid.
	 * @param jsonString A JSON object containing the fields user(email) and password.
	 * @return Returns a JSON object containing the fields token and expire-date.
	 */
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response LoginUser(String jsonString){
		return null;
	}

	/**
	 * Validates a user token.
	 * @param jsonString A JSON object containing the fields token and pseudonym.
	 * @return Returns a JSON object containing the fields expire-date and success.
	 */
	@POST
	@Path("/auth")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response ValidateToken(String jsonString){
		return null;
	}
}
