package register.test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.notNullValue;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.grizzly.http.SelectorThread;

import io.restassured.RestAssured;
import register.server.Config;
import register.server.Service;
import register.server.StorageProviderMongoDB;

public class TestRegisterServer {
    SelectorThread threadSelector;

    @Before
    public void setUp() throws Exception {
        // Setup dependencies
        Config.init(new String[]{
                "-mongoURI", "mongodb://testmongodb:27017/",
                "-dbName", "regTest",
                "-loginURI","http://localhost:5001/"});

        StorageProviderMongoDB sp = new StorageProviderMongoDB();
        StorageProviderMongoDB.Init();
        sp.clearForTest();
        TestLoginServer.start();

        // Setup RestAssured
        RestAssured.baseURI = "http://localhost";
        RestAssured.basePath = "/";
        RestAssured.port = 5002;
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
        // Register new user
        expect().statusCode(200).contentType(MediaType.APPLICATION_JSON).body("success", notNullValue()).given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(("{'pseudonym': 'tom','password': 'tom', 'user': 'tom@web.de'}").replace('\'', '"'))
                .when().put("/register");
        // Login the new user
        String token = TestLoginServer.LoginUser("tom","tom");

        JSONObject json = new JSONObject();
        json.put("token", token);
        json.put("getownprofile", "tom");
        expect().statusCode(200).contentType(MediaType.APPLICATION_JSON).body("name", notNullValue())
                .body("email", notNullValue()).body("contact", notNullValue()).given()
                .contentType(MediaType.APPLICATION_JSON).body(json.toString()).when().post("/profile");
        json = new JSONObject();
        json.put("token", "hallo");
        json.put("getownprofile", "susi");
        expect().statusCode(400).given().contentType(MediaType.APPLICATION_JSON).body(json.toString()).when()
                .post("/profile");

    }


}
