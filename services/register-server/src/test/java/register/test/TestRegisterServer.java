package register.test;

import static io.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.notNullValue;

import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.grizzly.http.SelectorThread;

import io.restassured.RestAssured;
import register.server.Service;
import register.server.StorageProviderMongoDB;

public class TestRegisterServer {
	SelectorThread threadSelector;
	String tokenBob;
	private static String mongoURL = "mongodb://141.19.142.57:27017";

	@Before
	public void setUp() {
		TestLoginServer.start();
		RestAssured.baseURI = "http://localhost";
		RestAssured.basePath = "/";
		RestAssured.port = 5006;
		StorageProviderMongoDB sp = new StorageProviderMongoDB();
		StorageProviderMongoDB.Init(mongoURL);
		Service.storageProvider = sp;
		threadSelector = Service.startRegistrationServer(RestAssured.baseURI + ":" + RestAssured.port + "/");
	}

	@After
	public void tearDown() {
		TestLoginServer.stop();
		Service.stopRegisterServer(threadSelector);
	}

	/**
	 * This test will check the registration with one valid registration and 4
	 * invalid registrations to
	 */
	@Test
	public void testRegistration() {
		expect().statusCode(200).contentType(MediaType.APPLICATION_JSON).body("success", notNullValue()).given()
				.contentType(MediaType.APPLICATION_JSON)
				.body(("{'pseudonym': 'bob','password': 'halloIchbinBob', 'user': 'bob@web.de'}").replace('\'', '"'))
				.when().put("/register");
		expect().statusCode(418).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'pseudonym': 'bob','password': 'halloIchbinBob', 'user': 'bob@web.de'}").replace('\'', '"'))
				.when().put("/register");

		expect().statusCode(400).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'password': 'halloIchbinBob', 'user': 'bob@web.de'}").replace('\'', '"')).when()
				.put("/register");

		expect().statusCode(400).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'pseudonym': 'bob', 'user': 'bob@web.de'}").replace('\'', '"')).when().put("/register");

		expect().statusCode(400).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'pseudonym': 'bob','password': 'halloIchbinBob'}").replace('\'', '"')).when().put("/register");
	}

	/**
	 * This test will login bob and then try to authanticate whith the responsed
	 * token.
	 */
	@Test
	public void testProfile() {

		JSONObject json = new JSONObject();
		json.put("token", tokenBob);
		json.put("getownprofile", "bob");

		expect().statusCode(200).contentType(MediaType.APPLICATION_JSON).body("name", notNullValue())
				.body("email", notNullValue()).body("contact", notNullValue()).given()
				.contentType(MediaType.APPLICATION_JSON).body(json.toString()).when().post("/profile");

		json = new JSONObject();
		json.put("token", tokenBob + "fg");
		json.put("getownprofile", "bofgb");

		expect().statusCode(400).given().contentType(MediaType.APPLICATION_JSON).body(json.toString()).when()
				.post("/profile");

	}

}
