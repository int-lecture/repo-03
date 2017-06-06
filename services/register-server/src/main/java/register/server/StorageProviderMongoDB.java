package register.server;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import services.common.StorageProviderCoreMongoDB;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

public class StorageProviderMongoDB extends StorageProviderCoreMongoDB {
    private StorageProviderMongoDB() throws Exception {
        super();
    }

    public static synchronized void init() throws Exception {
        StorageProviderCoreMongoDB.init(
                Config.getSettingValue(Config.mongoURI),
                Config.getSettingValue(Config.dbName));
    }

    static boolean createNewUser(User user) {
        if (userExists(user.getPseudonym(), user.getEmail())) {
            return false;
        }

        MongoCollection<Document> collection = database.getCollection(Config.getSettingValue(Config.dbAccountCollection));
        Document doc = new Document("user", user.getEmail()).append("password", user.getHashedPassword())
                .append("user", user.getEmail()).append("pseudonym", user.getPseudonym());
        collection.insertOne(doc);
        return true;
    }

    static User getUserProfile(String name) {
        MongoCollection<Document> collection = database.getCollection(Config.getSettingValue(Config.dbAccountCollection));
        Document doc = collection.find(eq("pseudonym", name)).first();
        if (doc == null) {
            return null;
        }

        String email = doc.getString("user");
        String hashedPW = doc.getString("password");
        @SuppressWarnings("rawtypes")
        List dbContacts = doc.get("contacts", List.class);
        List<String> contacts = new ArrayList<>();
        if (dbContacts != null) {
            for (Object contact : dbContacts) {
                contacts.add(contact.toString());
            }
        }

        return new User(name, hashedPW, email, contacts);
    }

    static boolean userExists(String name, String email) {
        MongoCollection<Document> collection = database.getCollection(Config.getSettingValue(Config.dbAccountCollection));
        Document doc = collection.find(or(eq("pseudonym", name), eq("user", email))).first();
        return doc != null;
    }

    public static void clearForTest() {
        DeleteCollection(Config.getSettingValue(Config.dbAccountCollection));
    }

}
