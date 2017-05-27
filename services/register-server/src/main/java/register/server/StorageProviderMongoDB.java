package register.server;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import register.server.IStorageProvider;
import register.server.User;

public class StorageProviderMongoDB implements IStorageProvider {

	private static final String userDB = "benutzer";
	private static final String accountCollection = "account";
	private static String mongoURL = "mongodb://141.19.142.57:27017";

	/** Client to be used. */
	private static MongoClient mongoClient;

	/** Mongo database. */
	private static MongoDatabase database;

	public static void Init(String mongoURL) {
		StorageProviderMongoDB.mongoURL = mongoURL;
		mongoClient = new MongoClient(new MongoClientURI(StorageProviderMongoDB.mongoURL));
		database = mongoClient.getDatabase(userDB);
	}

	@Override
	public boolean createNewUser(User user) {
		if (userExists(user.getPseudonym(), user.getEmail())) {
			return false;
		}

		MongoCollection<Document> collection = database.getCollection(accountCollection);
		Document doc = new Document("user", user.getEmail()).append("password", user.getHashedPassword())
				.append("user", user.getEmail()).append("pseudonym", user.getPseudonym());
		collection.insertOne(doc);
		return true;
	}

	@Override
	public User getUserProfile(String name) {
		MongoCollection<Document> collection = database.getCollection(accountCollection);
		Document doc = collection.find(eq("pseudonym", name)).first();
		if(doc == null) {
			return null;
		}

		String email = doc.getString("user");
		String hashedPW = doc.getString("password");
		@SuppressWarnings("rawtypes")
		List dbContacts = doc.get("contacts",List.class);
		List<String> contacts = new ArrayList<>();
		if (dbContacts != null) {
			for (Object contact : dbContacts) {
				contacts.add(contact.toString());
			}
		}

		return new User(name, hashedPW, email, contacts);
	}

	@Override
	public boolean userExists(String name, String email) {
		MongoCollection<Document> collection = database.getCollection(accountCollection);
		Document doc = collection.find(or(eq("pseudonym", name),eq("user",email))).first();
		return doc != null;
	}

}