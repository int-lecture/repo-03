package register.server;

import static com.mongodb.client.model.Filters.*;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class StorageProviderMongoDB implements IStorageProvider {
	/** Client to be used. */
	private static MongoClient mongoClient;

	/** Mongo database. */
	private static MongoDatabase database;

	public static void Init() {
		mongoClient = new MongoClient(new MongoClientURI(Config.getSettingValue(Config.mongoURI)));
		database = mongoClient.getDatabase(Config.getSettingValue(Config.dbName));
		// Used to check for valid connection!
		try {
			mongoClient.getDatabaseNames();
		} catch (Exception e) {
			System.out.printf("Could not connect to mongodb at %s because of %s \n",Config.getSettingValue(Config.mongoURI),e.getMessage());
			System.exit(-1);
		}
		System.out.println("MongoDB storage provider initialized.");
	}

	@Override
	public boolean createNewUser(User user) {
		if (userExists(user.getPseudonym(), user.getEmail())) {
			return false;
		}

		MongoCollection<Document> collection = database.getCollection(Config.getSettingValue(Config.dbAccountCollection));
		Document doc = new Document("user", user.getEmail()).append("password", user.getHashedPassword())
				.append("user", user.getEmail()).append("pseudonym", user.getPseudonym());
		collection.insertOne(doc);
		return true;
	}

	@Override
	public User getUserProfile(String name) {
		MongoCollection<Document> collection = database.getCollection(Config.getSettingValue(Config.dbAccountCollection));
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
		MongoCollection<Document> collection = database.getCollection(Config.getSettingValue(Config.dbAccountCollection));
		Document doc = collection.find(or(eq("pseudonym", name),eq("user",email))).first();
		return doc != null;
	}

	public void clearForTest()	{
		MongoCollection<Document> collection = database.getCollection(Config.getSettingValue(Config.dbAccountCollection));
		// Deletes all items in the collection
		collection.deleteMany(ne("remove","all"));
	}

}
