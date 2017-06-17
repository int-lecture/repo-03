package register.test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.notNullValue;

import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.grizzly.http.SelectorThread;

import io.restassured.RestAssured;
import register.server.Config;
import register.server.Service;
import register.server.StorageProviderMongoDB;
import services.common.StorageException;

import java.util.HashMap;
import java.util.Map;

public class TestRegisterServer {

    SelectorThread threadSelector;
    private static Map<String, String> expectedCORSHeaders = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        // Setup dependencies
        Config.init(new String[]{
                "-mongoURI", "mongodb://testmongodb:27017/",
                "-dbName", "regTest",
                "-loginURI", "http://localhost:5001/"});
        expectedCORSHeaders.put("Access-Control-Allow-Origin", "*");


        try {
            StorageProviderMongoDB.init();
        } catch (StorageException e) {
            System.out.println("Storage provider already initialized.");
        }

        StorageProviderMongoDB.clearForTest();
        SetupLoginServer.start();

        // Setup RestAssured
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "/";
        RestAssured.port = 5002;
        threadSelector = Service.startRegistrationServer(RestAssured.baseURI + ":" + RestAssured.port + "/");
    }

    @After
    public void tearDown() {
        SetupLoginServer.stop();
        Service.stopRegisterServer(threadSelector);
    }

    /**
     * This test will check the registration with one valid registration and 4
     * invalid registrations to
     */
    @Test
    public void testRegistration() {
        expect().statusCode(200).headers(expectedCORSHeaders).contentType(MediaType.APPLICATION_JSON).body("success", notNullValue()).given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(("{'pseudonym': 'bob','password': 'halloIchbinBob', 'user': 'bob@web.de'}").replace('\'', '"'))
                .when().put("/register");
        expect().statusCode(418).headers(expectedCORSHeaders).given().contentType(MediaType.APPLICATION_JSON)
                .body(("{'pseudonym': 'bob','password': 'halloIchbinBob', 'user': 'bob@web.de'}").replace('\'', '"'))
                .when().put("/register");

        expect().statusCode(400).headers(expectedCORSHeaders).given().contentType(MediaType.APPLICATION_JSON)
                .body(("{'password': 'halloIchbinBob', 'user': 'bob@web.de'}").replace('\'', '"')).when()
                .put("/register");

        expect().statusCode(400).headers(expectedCORSHeaders).given().contentType(MediaType.APPLICATION_JSON)
                .body(("{'pseudonym': 'bob', 'user': 'bob@web.de'}").replace('\'', '"')).when().put("/register");

        expect().statusCode(400).headers(expectedCORSHeaders).given().contentType(MediaType.APPLICATION_JSON)
                .body(("{'pseudonym': 'bob','password': 'halloIchbinBob'}").replace('\'', '"')).when().put("/register");
    }

    /**
     * This test will login bob and then try to authanticate whith the responsed
     * token.
     */
    @Test
    public void testProfile() {
        // Register new user
        expect().statusCode(200).headers(expectedCORSHeaders).contentType(MediaType.APPLICATION_JSON).body("success", notNullValue()).given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(("{'pseudonym': 'tom','password': 'tom', 'user': 'tom@web.de'}").replace('\'', '"'))
                .when().put("/register");
        // Login the new user
        String token = SetupLoginServer.LoginUser("tom@web.de", "tom");

        JSONObject json = new JSONObject();
        json.put("token", token);
        json.put("getownprofile", "tom");
        expect().statusCode(200).headers(expectedCORSHeaders).contentType(MediaType.APPLICATION_JSON).body("name", notNullValue())
                .body("email", notNullValue()).body("contact", notNullValue()).given()
                .contentType(MediaType.APPLICATION_JSON).body(json.toString()).when().post("/profile");
        json = new JSONObject();
        json.put("token", "hallo");
        json.put("getownprofile", "susi");
        expect().statusCode(403).headers(expectedCORSHeaders).given().contentType(MediaType.APPLICATION_JSON).body(json.toString()).when()
                .post("/profile");

    }


    @Test
    public void testAddContact() {
        // Register new user
        expect().statusCode(200).headers(expectedCORSHeaders).contentType(MediaType.APPLICATION_JSON).body("success", notNullValue()).given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(("{'pseudonym': 'tom','password': 'tom', 'user': 'tom@web.de'}").replace('\'', '"'))
                .when().put("/register");
        expect().statusCode(200).headers(expectedCORSHeaders).contentType(MediaType.APPLICATION_JSON).body("success", notNullValue()).given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(("{'pseudonym': 'bob','password': '1234', 'user': 'bob@web.de'}").replace('\'', '"'))
                .when().put("/register");
        // Login the new user
        String token = SetupLoginServer.LoginUser("tom@web.de", "tom");
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'token':'" + token + "','pseudonym':'tom','newContact':'bob'}")
                .when()
                .put("/addcontact")

                .then()
                .headers(expectedCORSHeaders)
                .statusCode(200);
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'token':'abcd','pseudonym':'tom','newContact':'bob'}")
                .when()
                .put("/addcontact")

                .then()
                .headers(expectedCORSHeaders)
                .statusCode(403);
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'token':'" + token + "','pseudonym':'tom','newContact':'tom'}")
                .when()
                .put("/addcontact")

                .then()
                .headers(expectedCORSHeaders)
                .statusCode(400);
        // Add no existing user
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'token':'" + token + "','pseudonym':'tom','newContact':'susi'}")
                .when()
                .put("/addcontact")

                .then()
                .headers(expectedCORSHeaders)
                .statusCode(400);
        // contact already in list
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'token':'" + token + "','pseudonym':'tom','newContact':'bob'}")
                .when()
                .put("/addcontact")

                .then()
                .headers(expectedCORSHeaders)
                .statusCode(403);
    }
}
