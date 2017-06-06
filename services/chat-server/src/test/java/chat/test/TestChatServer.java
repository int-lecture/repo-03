package chat.test;

import javax.ws.rs.core.MediaType;
import static io.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.notNullValue;

import chat.server.Service;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.restassured.RestAssured;

import java.util.HashMap;
import java.util.Map;

public class TestChatServer {

	private static Map<String,String> expectedCORSHeaders = new HashMap<>();

	@Before
	public void setUp() throws Exception {
		chat.server.Config.init(new String[]{
				"-mongoURI", "mongodb://testmongodb:27017/",
				"-dbName", "regTest",
				"-loginURI","http://localhost:5001/"
		});
		expectedCORSHeaders.put("Access-Control-Allow-Origin", "*");
		SetupLoginServer.start();
		RestAssured.port = 5000;
		RestAssured.baseURI = "http://localhost";
		RestAssured.basePath = "/";
		Service.startChatServer(RestAssured.baseURI + ":" + RestAssured.port + "/");
	}

	@After
	public void tearDown() {
		Service.stopChatServer();
		SetupLoginServer.stop();
	}

	/**
	 * This test will check the login with one valid user and 2 invalid users to
	 * test the BadRequest and 1 valid user with wrong password, to check the
	 * unauthorized.
	 */
	@Test
	public void testSend() {
		String tokenBob= SetupLoginServer.LoginUser("bob@web.de", "HalloIchbinBob");
		String tokenTom= SetupLoginServer.LoginUser("tom@web.de", "HalloIchbinTom");
		// testing a valid message
		expect().statusCode(201).headers(expectedCORSHeaders).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'to':'tom','from':'bob','date':'2017-04-26T11:30:30+0200','text':'Test1','token':" + "'"+tokenBob+"'"
						+ "}".replace('\'', '"')))
				.when().put("/send");
		
		expect().statusCode(201).headers(expectedCORSHeaders).given().contentType(MediaType.APPLICATION_JSON)
		.body(("{'to':'bob','from':'tom','date':'2017-04-26T11:30:30+0200','text':'Test1','token':" + "'"+tokenTom+"'"
				+ "}".replace('\'', '"')))
		.when().put("/send");
		// testing a wrong password
		expect().statusCode(401).headers(expectedCORSHeaders).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'to':'tom','from':'bob','date':'2017-04-26T11:30:30+0200','text':'Test1','token': 'YXNkaCBhc2R6YWllIHVqa2RzaCBzYWlkaGFleiA'}"
						.replace('\'', '"')))
				.when().put("/send");
		// testing all missing fields which are possible
		expect().statusCode(400).headers(expectedCORSHeaders).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'to':'tom','from':'bob','text':'Test1','token': 'YXNkaCBhc2R6YWllIHVqa2RzaCBzYWlkaGFleiA'}"
						.replace('\'', '"')))
				.when().put("/send");

		expect().statusCode(400).headers(expectedCORSHeaders).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'to':'tom','date':'2017-04-26T11:30:30+0200','text':'Test1','token': 'YXNkaCBhc2R6YWllIHVqa2RzaCBzYWlkaGFleiA'}"
						.replace('\'', '"')))
				.when().put("/send");

		expect().statusCode(400).headers(expectedCORSHeaders).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'from':'bob','date':'2017-04-26T11:30:30+0200','text':'Test1','token': 'YXNkaCBhc2R6YWllIHVqa2RzaCBzYWlkaGFleiA'}"
						.replace('\'', '"')))
				.when().put("/send");

		expect().statusCode(400).headers(expectedCORSHeaders).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'to':'tom','from':'bob','date':'2017-04-26T11:30:30+0200','token': 'YXNkaCBhc2R6YWllIHVqa2RzaCBzYWlkaGFleiA'}"
						.replace('\'', '"')))
				.when().put("/send");

		expect().statusCode(400).headers(expectedCORSHeaders).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'to':'tom','from':'bob','date':'2017-04-26T11:30:30+0200','text':'Test1'}".replace('\'', '"')))
				.when().put("/send");

	}

	/**
	 * This test will login bob and then try to authanticate whith the responsed
	 * token.
	 */
	@Test
	public void testMessages() {
		String tokenTom= SetupLoginServer.LoginUser("tom@web.de", "HalloIchbinTom");
		String tokenBob= SetupLoginServer.LoginUser("bob@web.de", "HalloIchbinBob");
		// testing a correct message request
		expect().headers(expectedCORSHeaders).statusCode(200).contentType(MediaType.APPLICATION_JSON).header("Access-Control-Allow-Origin", "*").body("messages", notNullValue())
		.given().header("Authorization", tokenTom).when().get("/messages/tom/0");

		// testing a correct message request when no messages were sent
		expect().statusCode(204).headers(expectedCORSHeaders).given().header("Authorization", tokenBob).when().get("/messages/bob/8");

		// testing an invalid user
		expect().statusCode(400).headers(expectedCORSHeaders).given().header("Authorization", tokenBob).when().get("/messages/peter/0");

		// testing a invalid token
		expect().statusCode(401).headers(expectedCORSHeaders).given().header("Authorization", tokenTom).when().get("/messages/bob/0");
	}

	@Test
	public void testCORSOptions() {
		expect().statusCode(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").when().options("/send");

		expect().statusCode(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").when().options("/messages/bob");

		expect().statusCode(200).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").when().options("/messages/bob/12");
	}
}
