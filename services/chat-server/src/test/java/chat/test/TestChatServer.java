package chat.test;

import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;

import chat.server.Config;
import chat.server.Service;
import chat.server.StorageProviderMongoDB;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.restassured.RestAssured;
import services.common.StorageException;

import java.util.HashMap;
import java.util.Map;

public class TestChatServer {

    private static Map<String, String> expectedCORSHeaders = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        Config.init(new String[]{
                "-mongoURI", "mongodb://testmongodb:27017/",
                "-dbName", "msgTest",
                "-loginURI", "http://localhost:5001"
        });
        expectedCORSHeaders.put("Access-Control-Allow-Origin", "*");

        try {
            StorageProviderMongoDB.init();
        } catch (StorageException e) {
            System.out.println("Storage provider already initialized.");
        }

        SetupLoginServer.start();


        StorageProviderMongoDB.clearForTests();
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
        String tokenBob = SetupLoginServer.LoginUser("bob@web.de", "HalloIchbinBob");
        String tokenTom = SetupLoginServer.LoginUser("tom@web.de", "HalloIchbinTom");

        // testing a valid message
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'to':'tom','from':'bob','date':'2017-04-26T11:30:30+0200','text':'Test1','token':" + "'" + tokenBob + "'" + "}")

                .when()
                .put("/send")

                .then()
                .statusCode(201)
                .body("sequence", equalTo(1))
                .body("date", equalTo("2017-04-26T11:30:30+0200"))
                .headers(expectedCORSHeaders);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'to':'tom','from':'bob','date':'2017-04-26T11:30:30+0200','text':'Test1','token':" + "'" + tokenBob + "'" + "}")

                .when()
                .put("/send")

                .then()
                .statusCode(201)
                .body("sequence", equalTo(2))
                .body("date", equalTo("2017-04-26T11:30:30+0200"))
                .headers(expectedCORSHeaders);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'to':'bob','from':'tom','date':'2017-04-26T11:30:30+0200','text':'Test1','token':" + "'" + tokenTom + "'" + "}")

                .when()
                .put("/send")

                .then()
                .statusCode(201)
                .body("sequence", equalTo(1))
                .body("date", equalTo("2017-04-26T11:30:30+0200"))
                .headers(expectedCORSHeaders);
        // testing a wrong token
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'to':'tom','from':'bob','date':'2017-04-26T11:30:30+0200','text':'Test1','token':" + "'nothing'" + "}")

                .when()
                .put("/send")

                .then()
                .statusCode(401)
                .body(equalTo("Invalid Token"))
                .headers(expectedCORSHeaders);
        // testing all missing fields which are possible
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'to':'tom','from':'bob','date':'2017-04-26T11:30:30+0200','text':'Test1'}")

                .when()
                .put("/send")

                .then()
                .statusCode(400)
                .body(equalTo("Message was incomplete"))
                .headers(expectedCORSHeaders);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'to':'tom','from':'bob','date':'2017-04-26T11:30:30+0200','token':" + "'" + tokenBob + "'" + "}")

                .when()
                .put("/send")

                .then()
                .statusCode(400)
                .body(equalTo("Message was incomplete"))
                .headers(expectedCORSHeaders);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'to':'tom','from':'bob','text':'Test1','token':" + "'" + tokenBob + "'" + "}")

                .when()
                .put("/send")

                .then()
                .statusCode(400)
                .body(equalTo("Message was incomplete"))
                .headers(expectedCORSHeaders);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'to':'tom','date':'2017-04-26T11:30:30+0200','text':'Test1','token':" + "'" + tokenBob + "'" + "}")

                .when()
                .put("/send")

                .then()
                .statusCode(400)
                .body(equalTo("Message was incomplete"))
                .headers(expectedCORSHeaders);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'from':'bob','date':'2017-04-26T11:30:30+0200','text':'Test1','token':" + "'" + tokenBob + "'" + "}")

                .when()
                .put("/send")

                .then()
                .statusCode(400)
                .body(equalTo("Message was incomplete"))
                .headers(expectedCORSHeaders);

    }

    /**
     * This test will login bob and then try to authanticate whith the responsed
     * token.
     */
    @Test
    public void testMessages() {
        String tokenTom = SetupLoginServer.LoginUser("tom@web.de", "HalloIchbinTom");
        String tokenBob = SetupLoginServer.LoginUser("bob@web.de", "HalloIchbinBob");
        String tokenHans = SetupLoginServer.LoginUser("hans@web.de", "HalloIchbinHans");

        // Test user with no message received and no prior interaction at all
        given()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenBob)

                .when()
                .get("/messages/bob/0")

                .then()
                .statusCode(204);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenHans)

                .when()
                .get("/messages/hans/0")

                .then()
                .statusCode(204);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'to':'tom','from':'bob','date':'2017-04-26T11:30:30+0200','text':'Test1','token':" + "'" + tokenBob + "'" + "}")

                .when()
                .put("/send")

                .then()
                .statusCode(201)
                .body("sequence", equalTo(1))
                .body("date", equalTo("2017-04-26T11:30:30+0200"))
                .headers(expectedCORSHeaders);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'to':'tom','from':'hans','date':'2017-04-26T11:30:30+0200','text':'Test2','token':" + "'" + tokenHans + "'" + "}")

                .when()
                .put("/send")

                .then()
                .statusCode(201)
                .body("sequence", equalTo(2))
                .body("date", equalTo("2017-04-26T11:30:30+0200"))
                .headers(expectedCORSHeaders);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{'to':'tom','from':'bob','date':'2017-04-26T11:30:30+0200','text':'Test3','token':" + "'" + tokenBob + "'" + "}")

                .when()
                .put("/send")

                .then()
                .statusCode(201)
                .body("sequence", equalTo(3))
                .body("date", equalTo("2017-04-26T11:30:30+0200"))
                .headers(expectedCORSHeaders);

        // testing a correct message request
        for (int i = 0; i < 3; i++) {
            given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", tokenTom)

                    .when()
                    .get("/messages/tom/0")

                    .then()
                    .statusCode(200)
                    .body("size()", is(3))
                    .headers(expectedCORSHeaders);
        }

        // testing a retrieving a single a message and message removal
        for (int i = 0; i < 3; i++) {
            given()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", tokenTom)

                    .when()
                    .get("/messages/tom/1")

                    .then()
                    .statusCode(200)
                    .body("size()", is(2))
                    .body("[0].to", is("tom"))
                    .body("[0].from", is("hans"))
                    .body("[0].text", is("Test2"))
                    .body("[0].sequence", is(2))
                    .body("[0].date", is("2017-04-26T11:30:30+0200"))
                    .headers(expectedCORSHeaders);

        }

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenTom)

                .when()
                .get("/messages/tom/3")

                .then()
                .statusCode(204);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenTom)

                .when()
                .get("/messages/tom/1")

                .then()
                .statusCode(204);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenTom)

                .when()
                .get("/messages/tom/0")

                .then()
                .statusCode(204);
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
