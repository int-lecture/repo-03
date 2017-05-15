package login.server;

import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Storage provider for a MongoDB.
 */
class StorageProviderMongoDB {

	private static final String MONGO_URL = "mongodb://docker-03:27017";
    /** URI to the MongoDB instance. */
    private static MongoClientURI connectionString =
            new MongoClientURI(MONGO_URL);

    /** Client to be used. */
    private static MongoClient mongoClient = new MongoClient(connectionString);

    /** Mongo database. */
    private static MongoDatabase database = mongoClient.getDatabase("chat");

    /**
     * @see var.chat.server.persistence.StorageProvider#retrieveMessages(java.lang.String, long, boolean)
     */
    public synchronized String retrieveLoginData(String pseudonym) {

        MongoCollection<Document> collection = database.getCollection("user");

        // Retreive the hashed password
        String hashedPassword = collection.find(eq("pseudonym", pseudonym)).first().getString("password");

        // No messages for user there
        if (hashedPassword.isEmpty()) {
            return null;
        }

        return hashedPassword;
    }

    /**
     * @see var.chat.server.persistence.StorageProvider#clearForTest()
     */
    public void clearForTest() {
        database.getCollection("messages").drop();
        database.getCollection("sequences").drop();
    }
}