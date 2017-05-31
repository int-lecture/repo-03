package register.test;

import com.sun.jersey.api.client.Client;
import login.server.Service;
import org.json.JSONObject;
import register.server.Config;

import javax.ws.rs.core.MediaType;

public class TestLoginServer {

	public static void start() throws Exception {
		login.server.Config.init(new String[0]);
		Service.startLoginServer(Config.getSettingValue(Config.loginURI));
	}

	public static void stop() {
		Service.stopLoginServer();
	}

	public static String LoginUser(String username, String password) {
		JSONObject obj = new JSONObject();
		obj.put("user", username);
		obj.put("password", password);


		// TODO : Maybe use one static client?
		Client webClient = new Client();
		String response = webClient.resource(Config.getSettingValue(Config.loginURI) + "login")
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON)
				.post(String.class, obj.toString());
		webClient.destroy();
		JSONObject jo = new JSONObject(response);
		return jo.getString("token");
	}
}
