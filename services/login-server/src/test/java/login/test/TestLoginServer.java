package login.test;

import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import login.server.Config;
import login.server.User;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.restassured.response.Response;
import login.server.Service;
import login.server.StorageProviderMongoDB;
import services.common.StorageException;

import java.util.HashMap;
import java.util.Map;

public class TestLoginServer {

    private static Map<String,String> expectedCORSHeaders = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        // Setup dependencies
        Config.init(new String[]{
                "-mongoURI", "mongodb://testmongodb:27017/",
                "-dbName", "regTest"});
        expectedCORSHeaders.put("Access-Control-Allow-Origin", "*");

        try {
            StorageProviderMongoDB.init();
        } catch (StorageException e) {
            System.out.println("Storage provider already initialized.");
        }

        //Fill db
        StorageProviderMongoDB.clearForTest(new User[]{
                new User("bob@web.de", "HalloIchbinBob", "bob")
        });

        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "/";
        RestAssured.port = 5001;

        Service.startLoginServer(Config.getSettingValue(Config.baseURI));
    }

    @After
    public void tearDown() {
        Service.stopLoginServer();
    }

    /**
     * This test will check the login with one valid user and 2 invalid users to
     * test the BadRequest and 1 valid user with wrong password, to check the
     * unauthorized.
     */
    @Test
    public void testLogin() {
        expect().statusCode(200).headers(expectedCORSHeaders).contentType(MediaType.APPLICATION_JSON).body("token", notNullValue())
                .body("expire-date", notNullValue()).given().contentType(MediaType.APPLICATION_JSON)
                .body(("{'user': 'bob@web.de', 'password': 'HalloIchbinBob', 'pseudonym': 'bob'}".replace('\'', '"')))
                .when().post("/login");

        expect().statusCode(400).headers(expectedCORSHeaders).contentType(MediaType.APPLICATION_JSON).given().contentType(MediaType.APPLICATION_JSON)
                .body(("{ 'password': 'HalloIchbinBob', 'pseudonym': 'bob'}".replace('\'', '"'))).when().post("/login");

        expect().statusCode(200).headers(expectedCORSHeaders).contentType(MediaType.APPLICATION_JSON).given().contentType(MediaType.APPLICATION_JSON)
                .body(("{ 'user': 'bob@web.de', 'password': 'HalloIchbinBob'}".replace('\'', '"'))).when()
                .post("/login");

        expect().statusCode(401).headers(expectedCORSHeaders).contentType(MediaType.APPLICATION_JSON).given().contentType(MediaType.APPLICATION_JSON)
                .body(("{'user': 'bob@web.de', 'password': 'HalloIfsegbinBob', 'pseudonym': 'bob'}".replace('\'', '"')))
                .when().post("/login");
    }

    /**
     * This test will login bob and then try to authanticate whith the responsed
     * token.
     */
    @Test
    public void testValidate() {
        Response resp = expect().statusCode(200).headers(expectedCORSHeaders).contentType(MediaType.APPLICATION_JSON).body("token", notNullValue())
                .body("expire-date", notNullValue()).given().contentType(MediaType.APPLICATION_JSON)
                .body(("{'user': 'bob@web.de', 'password': 'HalloIchbinBob', 'pseudonym': 'bob'}".replace('\'', '"')))
                .when().post("/login");
        String token = resp.path("token").toString();

        JSONObject json = new JSONObject();
        json.put("token", token);
        json.put("pseudonym", "bob");

        expect().statusCode(200).headers(expectedCORSHeaders).contentType(MediaType.APPLICATION_JSON).body("success", notNullValue())
                .body("expire-date", notNullValue()).given().contentType(MediaType.APPLICATION_JSON)
                .body(json.toString()).when().post("/auth");

        json.put("token", token);
        json.put("pseudonym", "bobX");

        expect().statusCode(401).headers(expectedCORSHeaders).contentType(MediaType.APPLICATION_JSON).given().contentType(MediaType.APPLICATION_JSON)
                .body(json.toString()).when().post("/auth");

        json.put("token", token + "X");
        json.put("pseudonym", "bob");

        expect().statusCode(401).headers(expectedCORSHeaders).contentType(MediaType.APPLICATION_JSON).given().contentType(MediaType.APPLICATION_JSON)
                .body(json.toString()).when().post("/auth");

    }

}
