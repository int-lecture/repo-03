package chat.test;

import javax.ws.rs.core.MediaType;

import chat.server.Config;
import com.sun.jersey.api.client.Client;
import login.server.Service;

import login.server.StorageProviderMongoDB;
import login.server.User;
import org.json.JSONObject;

public class SetupLoginServer {

	public static void start() throws Exception {
		login.server.Config.init(new String[]{
				"-mongoURI", "mongodb://testmongodb:27017/",
				"-dbName", "regTest"
		});

		try {
			StorageProviderMongoDB.init();
		} catch (Exception e) {
			System.out.println("Storage provider already initiliazed");
		}
		StorageProviderMongoDB.clearForTest(
				new User[]{
						new User("bob@web.de","HalloIchbinBob","bob"),
						new User("tom@web.de","HalloIchbinTom","tom")
				}
		);
		Service.startLoginServer(Config.getSettingValue(Config.loginURI));
	}

	public static void stop() {
		Service.stopLoginServer();
	}

	public static String LoginUser(String username, String password) {
		JSONObject obj = new JSONObject();
		obj.put("user", username);
		obj.put("password", password);

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
