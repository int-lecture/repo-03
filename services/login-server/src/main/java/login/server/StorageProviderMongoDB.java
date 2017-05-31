package login.server;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Storage provider for a MongoDB.
 */
class StorageProviderMongoDB {

    /**
     * Client to be used.
     */
    private static MongoClient mongoClient;

    /**
     * Mongo database.
     */
    private static MongoDatabase database;


    private static StorageProviderMongoDB sp;

    private StorageProviderMongoDB() {

    }

    public static synchronized StorageProviderMongoDB Init() {
        sp = new StorageProviderMongoDB();
        mongoClient = new MongoClient(
                new MongoClientURI(Config.getSettingValue(Config.mongoURI)));
        database = mongoClient.getDatabase("benutzer");
        // Used to check for valid connection!
        try {
            mongoClient.getDatabaseNames();
        } catch (Exception e) {
            System.out.printf("Could not connect to mongodb at %s because of %s \n",Config.getSettingValue(Config.mongoURI),e.getMessage());
            System.exit(-1);
        }
        System.out.println("MongoDB storage provider initialized.");
        return sp;
    }

    /**
     * Retrieves a user's info from the database. If Config.allowEmailLogin is true pseudonym can be null.
     *
     * @param username  The user's email.
     * @param pseudonym The user's pseudonym.
     * @return
     */
    public User retrieveUser(String username, String pseudonym) {

        MongoCollection<Document> collection = database.getCollection("account");
        Document doc = null;
        // Retrieve the hashed password
        if (pseudonym == null && Config.getSettingValue(Config.allowEmailLogin) == "true") {
            doc = collection.find(eq("user", username)).first();
        } else {
            doc = collection.find(and(eq("user", username), eq("pseudonym", pseudonym))).first();
        }

        if (doc == null) {
            return null;
        }

        User user = new User(username, doc.getString("password"), doc.getString("pseudonym"), true);
        return user;
    }

    /**
     *
     */
    public void saveToken(String token, String expirationDate, String pseudonym) {

        MongoCollection<Document> collection = database.getCollection("token");

        // add user to database
        Document doc = new Document("token", "" + token + "").append("expire-date", expirationDate).append("pseudonym",
                pseudonym);

        if (collection.find(eq("pseudonym", pseudonym)).first() != null) {
            collection.updateOne(eq("pseudonym", pseudonym), new Document("$set", doc));
        } else {
            collection.insertOne(doc);
        }
    }

    /**
     * Fetches a user's current Token from the database. If the token is not found or expired null is returned.
     *
     * @param pseudonym The user's pseudonym.
     * @param token     The user's current token.
     * @return The token's expiration date or null if the token was not found or is expired.
     */
    public Date retrieveToken(String pseudonym, String token) {
        MongoCollection<Document> collection = database.getCollection("token");
        // Retreive the tokeninformation
        Document doc = collection.find(and(eq("pseudonym", pseudonym), eq("token", token))).first();
        if (doc == null) {
            return null;
        }
        String expDateString = doc.getString("expire-date");
        if (expDateString == null) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(Service.ISO8601);
        Date expireDate = null;
        try {
            sdf.parse(expDateString);
        } catch (ParseException e) {
            // The token seems to be corrupted.
            collection.deleteOne(and(eq("pseudonym", pseudonym), eq("token", token)));
            return null;
        }

        if (Calendar.getInstance().getTime().before(expireDate))
            return expireDate;
        else
            return null;
    }

    /**
     *
     */
    public void deleteToken(String token) {
        MongoCollection<Document> collection = database.getCollection("token");
        collection.deleteOne(eq("token", token));
    }

    public void clearForTest() {

    }
}