package register.test;

import org.junit.Before;
import org.junit.Test;
import register.server.Config;
import register.server.StorageProviderMongoDB;
import register.server.User;
import services.common.StorageException;

import static org.junit.Assert.*;

public class StorageProviderMongoDBTest {
    @Before
    public void setUp() throws Exception {
        Config.init(new String[]{
                "-mongoURI", "mongodb://testmongodb:27017/",
                "-dbName", "regTest"
        });

        try {
            StorageProviderMongoDB.init();
        } catch (StorageException e) {
            System.out.println("Storage provider already initialized.");
        }

        StorageProviderMongoDB.clearForTest();
    }

    @Test
    public void createNewUser() throws Exception {
        // Multiple request with same information
        assertTrue(StorageProviderMongoDB.createNewUser(new User("Tom","Test123","abc@d.e")));
        assertFalse(StorageProviderMongoDB.createNewUser(new User("Tom","Test123","abc@d.e")));
        assertFalse(StorageProviderMongoDB.createNewUser(new User("Bob","Test123","abc@d.e")));
        assertFalse(StorageProviderMongoDB.createNewUser(new User("Tom","Test123","hallo@d.e")));
        assertTrue(StorageProviderMongoDB.createNewUser(new User("Bob","Test123","hallo@d.e")));
        // Empty Info
        assertFalse(StorageProviderMongoDB.createNewUser(new User("","Test123","no@d.e")));
        // DB can't verify password validity as its already hashed at this point.
        assertFalse(StorageProviderMongoDB.createNewUser(new User("","Test123","")));
    }

    @Test
    public void getUserProfile() throws Exception {
        assertTrue(StorageProviderMongoDB.createNewUser(new User("Tom","Test123","abc@d.e")));
        assertTrue(StorageProviderMongoDB.createNewUser(new User("Bob","Test123","hallo@d.e")));
        User tom = StorageProviderMongoDB.getUserProfile("Tom");
        assertNotNull(tom);
        assertNotNull(StorageProviderMongoDB.getUserProfile("Bob"));
        assertNull(StorageProviderMongoDB.getUserProfile("Ben"));
        // Verify saved information
        assertEquals("Tom",tom.getPseudonym());
        assertEquals("abc@d.e",tom.getEmail());
        assertNotEquals("Test123",tom.getHashedPassword());
        assertEquals(0,tom.getContacts().size());
        //Test Contact list
        assertTrue(StorageProviderMongoDB.newContact(tom,"Bob"));
        tom = StorageProviderMongoDB.getUserProfile("Tom");
        assertEquals("Tom",tom.getPseudonym());
        assertEquals("abc@d.e",tom.getEmail());
        assertNotEquals("Test123",tom.getHashedPassword());
        assertEquals(1,tom.getContacts().size());
        assertEquals("Bob",tom.getContacts().get(0));
    }

    @Test
    public void userExists() throws Exception {
        assertTrue(StorageProviderMongoDB.createNewUser(new User("Tom","Test123","abc@d.e")));
        assertTrue(StorageProviderMongoDB.userExists("Tom","abc@d.e"));
        assertTrue(StorageProviderMongoDB.userExists("Ben","abc@d.e"));
        assertTrue(StorageProviderMongoDB.userExists("Tom","hallo@d.e"));
        assertFalse(StorageProviderMongoDB.userExists("Carl","invoker@d.net"));
    }

    @Test
    public void newContact() throws Exception {
        assertTrue(StorageProviderMongoDB.createNewUser(new User("Tom","Test123","abc@d.e")));
        assertTrue(StorageProviderMongoDB.createNewUser(new User("Bob","Test123","hallo@d.e")));
        assertTrue(StorageProviderMongoDB.createNewUser(new User("Susi","Test123","123@d.e")));
        User tom = StorageProviderMongoDB.getUserProfile("Tom");
        User bob = StorageProviderMongoDB.getUserProfile("Bob");
        assertTrue(StorageProviderMongoDB.newContact(tom,"Bob"));
        assertFalse(StorageProviderMongoDB.newContact(tom,"Tom"));
        assertFalse(StorageProviderMongoDB.newContact(tom,""));
        assertTrue(StorageProviderMongoDB.newContact(bob,"Susi"));
        assertFalse(StorageProviderMongoDB.newContact(bob,"Susi"));
        assertFalse(StorageProviderMongoDB.newContact(bob,"Susi"));
        assertTrue(StorageProviderMongoDB.newContact(bob,"Tom"));
        tom = StorageProviderMongoDB.getUserProfile("Tom");
        bob = StorageProviderMongoDB.getUserProfile("Bob");
        assertEquals(1,tom.getContacts().size());
        assertEquals(2,bob.getContacts().size());
        assertTrue(tom.getContacts().contains("Bob"));
        assertTrue(bob.getContacts().contains("Tom"));
        assertTrue(bob.getContacts().contains("Susi"));
    }
}