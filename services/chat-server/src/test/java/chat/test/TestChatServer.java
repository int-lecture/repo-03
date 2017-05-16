package chat.test;

import javax.ws.rs.core.MediaType;
import static io.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.notNullValue;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class TestChatServer {
	@Before
	public void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.basePath = "/";
		RestAssured.port = 5000;
		// LoginServerMain.startGrizzly(RestAssured.baseURI + ":"+
		// RestAssured.basePath+ "/");
	}

	@After
	public void tearDown() {
		// LoginServer.stopGrizzly();
	}

	/**
	 * This test will check the login with one valid user and 2 invalid users to
	 * test the BadRequest and 1 valid user with wrong password, to check the
	 * unauthorized.
	 */
	@Test
	public void testSend() {
		Response resp = expect().statusCode(200).contentType(MediaType.APPLICATION_JSON).body("token", notNullValue())
				.body("expire-date", notNullValue()).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'user': 'bob@web.de', 'password': 'HalloIchbinBob', 'pseudonym': 'bob'}".replace('\'', '"')))
				.when().post("/login");
		String token = resp.path("token").toString();
		// testing a valid message
		expect().statusCode(201).contentType(MediaType.APPLICATION_JSON).body("date", notNullValue())
				.body("sequence", notNullValue()).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'to':'tom','from':'bob','date':'2017-04-26T11:30:30+0200','text':'Test1','token':" + token
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

		expect().statusCode(400).contentType(MediaType.APPLICATION_JSON).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{ 'password': 'HalloIchbinBob', 'pseudonym': 'bob'}".replace('\'', '"'))).when().post("/login");

		expect().statusCode(400).contentType(MediaType.APPLICATION_JSON).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{ 'user': 'bob@web.de', 'password': 'HalloIchbinBob'}".replace('\'', '"'))).when()
				.post("/login");

		expect().statusCode(403).contentType(MediaType.APPLICATION_JSON).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'user': 'bob@web.de', 'password': 'HalloIfsegbinBob', 'pseudonym': 'bob'}".replace('\'', '"')))
				.when().post("/login");
	}

	/**
	 * This test will login bob and then try to authanticate whith the responsed
	 * token.
	 */
	@Test
	public void testMessages() {
		Response resp = expect().statusCode(200).contentType(MediaType.APPLICATION_JSON).body("token", notNullValue())
				.body("expire-date", notNullValue()).given().contentType(MediaType.APPLICATION_JSON)
				.body(("{'user': 'bob@web.de', 'password': 'HalloIchbinBob', 'pseudonym': 'bob'}".replace('\'', '"')))
				.when().post("/login");
		String token = resp.path("token").toString();

		JSONObject json = new JSONObject();
		json.put("token", token);
		json.put("pseudonym", "bob");

		expect().statusCode(200).contentType(MediaType.APPLICATION_JSON).body("success", notNullValue())
				.body("expire-date", notNullValue()).given().contentType(MediaType.APPLICATION_JSON)
				.body(json.toString()).when().post("/auth");

		json.put("token", token);
		json.put("pseudonym", "bobX");

		expect().statusCode(403).contentType(MediaType.APPLICATION_JSON).body("success", notNullValue())
				.body("expire-date", notNullValue()).given().contentType(MediaType.APPLICATION_JSON)
				.body(json.toString()).when().post("/auth");

		json.put("token", token + "X");
		json.put("pseudonym", "bob");

		expect().statusCode(403).contentType(MediaType.APPLICATION_JSON).body("success", notNullValue())
				.body("expire-date", notNullValue()).given().contentType(MediaType.APPLICATION_JSON)
				.body(json.toString()).when().post("/auth");

	}

}
