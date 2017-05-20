package chat.test;

import javax.ws.rs.core.MediaType;
import static io.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import chat.server.Main;
import io.restassured.RestAssured;

public class TestChatServer {
	String tokenBob;
	String tokenTom;
	@Before
	public void setUp() {
		TestLoginServer.start();
		tokenBob=TestLoginServer.login("bob", "HalloIchbinBob").path("token").toString();
		tokenTom=TestLoginServer.login("tom", "HalloIchbinTom").path("token").toString();
		RestAssured.port = 5000;
		TestLoginServer.stop();
		RestAssured.baseURI = "http://localhost";
		RestAssured.basePath = "/";
		Main.starteChatServer(RestAssured.baseURI + ":" + RestAssured.port + "/");
		
	}

	@After
	public void tearDown() {
		Main.stopChatServer();
	}

	/**
	 * This test will check the login with one valid user and 2 invalid users to
	 * test the BadRequest and 1 valid user with wrong password, to check the
	 * unauthorized.
	 */
	@Test
	public void testSend() {
		// testing a valid message
		System.out.println(RestAssured.port);
		expect().statusCode(201).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'to':'tom','from':'bob','date':'2017-04-26T11:30:30+0200','text':'Test1','token':" + "'"+tokenBob+"'"
						+ "}".replace('\'', '"')))
				.when().put("/send");
		// testing a wrong password
		expect().statusCode(401).contentType(MediaType.APPLICATION_JSON).body("to", notNullValue())
				.body("from", notNullValue()).body("text", notNullValue()).body("date", notNullValue())
				.body("sequence", notNullValue()).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'to':'tom','from':'bob','date':'2017-04-26T11:30:30+0200','text':'Test1','token': 'YXNkaCBhc2R6YWllIHVqa2RzaCBzYWlkaGFleiA'}"
						.replace('\'', '"')))
				.when().put("/send");
		// testing all missing fields which are possible
		expect().statusCode(400).contentType(MediaType.APPLICATION_JSON).body("to", notNullValue())
				.body("from", notNullValue()).body("text", notNullValue()).body("date", notNullValue())
				.body("sequence", notNullValue()).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'to':'tom','from':'bob','text':'Test1','token': 'YXNkaCBhc2R6YWllIHVqa2RzaCBzYWlkaGFleiA'}"
						.replace('\'', '"')))
				.when().put("/send");

		expect().statusCode(400).contentType(MediaType.APPLICATION_JSON).body("to", notNullValue())
				.body("from", notNullValue()).body("text", notNullValue()).body("date", notNullValue())
				.body("sequence", notNullValue()).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'to':'tom','date':'2017-04-26T11:30:30+0200','text':'Test1','token': 'YXNkaCBhc2R6YWllIHVqa2RzaCBzYWlkaGFleiA'}"
						.replace('\'', '"')))
				.when().put("/send");

		expect().statusCode(400).contentType(MediaType.APPLICATION_JSON).body("to", notNullValue())
				.body("from", notNullValue()).body("text", notNullValue()).body("date", notNullValue())
				.body("sequence", notNullValue()).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'from':'bob','date':'2017-04-26T11:30:30+0200','text':'Test1','token': 'YXNkaCBhc2R6YWllIHVqa2RzaCBzYWlkaGFleiA'}"
						.replace('\'', '"')))
				.when().put("/send");

		expect().statusCode(400).contentType(MediaType.APPLICATION_JSON).body("to", notNullValue())
				.body("from", notNullValue()).body("text", notNullValue()).body("date", notNullValue())
				.body("sequence", notNullValue()).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'to':'tom','from':'bob','date':'2017-04-26T11:30:30+0200','token': 'YXNkaCBhc2R6YWllIHVqa2RzaCBzYWlkaGFleiA'}"
						.replace('\'', '"')))
				.when().put("/send");

		expect().statusCode(400).contentType(MediaType.APPLICATION_JSON).body("to", notNullValue())
				.body("from", notNullValue()).body("text", notNullValue()).body("date", notNullValue())
				.body("sequence", notNullValue()).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'to':'tom','from':'bob','date':'2017-04-26T11:30:30+0200','text':'Test1'}".replace('\'', '"')))
				.when().put("/send");
		// testing a JsonException
		expect().statusCode(500).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'to':'tom','from':'bob','date':'2017-04-26T11:30:3','text':'Test1','token': 'YXNkaCBhc2R6YWllIHVqa2RzaCBzYWlkaGFleiA'}"
						.replace('\'', '"')))
				.when().put("/send");

	}

	/**
	 * This test will login bob and then try to authanticate whith the responsed
	 * token.
	 */
	@Test
	public void testMessages() {

		// testing a correct message request
		expect().statusCode(200).contentType(MediaType.APPLICATION_JSON).body("messages", notNullValue())
				.header("Authentication", tokenTom).when().put("/messages/tom/0");

		// testing a correct message request when no messages were sent
		expect().statusCode(204).header("Authentication", tokenBob).when().put("/messages/bob/0");

		// testing an invalid user
		expect().statusCode(204).header("Authentication", tokenBob).when().put("/messages/peter/0");

		// testing a invalid token
		expect().statusCode(204).header("Authentication", tokenTom).when().put("/messages/bob/0");
	}

}
