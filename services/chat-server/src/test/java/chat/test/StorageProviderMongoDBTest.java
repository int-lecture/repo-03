package chat.test;

import chat.server.Config;
import chat.server.Message;
import chat.server.StorageProviderMongoDB;
import chat.server.User;
import org.junit.Before;
import org.junit.Test;
import services.common.StorageException;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class StorageProviderMongoDBTest {
    @Before
    public void setUp() throws Exception {
        Config.init(
                new String[]{
                        "-mongoURI", "mongodb://testmongodb:27017/",
                        "-dbName", "msgTest"
                }
        );

        try {
            StorageProviderMongoDB.init();
        } catch (StorageException e) {
            System.out.println("Storage provider already initialized.");
        }


        StorageProviderMongoDB.clearForTests();
    }

    @Test
    public void addMessage() throws Exception {
        User bob = new User("bob");
        User tom = new User("tom");
        Message msg = new Message("tom", "bob", new Date(), "addmessagetesttext", "token");
        assertEquals(1, StorageProviderMongoDB.addMessage(bob, msg));
        // Wrong receiver
        assertEquals(-1, StorageProviderMongoDB.addMessage(tom, msg));
        // Missing fields
        assertEquals(-1, StorageProviderMongoDB.addMessage(bob, new Message("tom", "", new Date(), "Hallo", "token")));
        assertEquals(-1, StorageProviderMongoDB.addMessage(bob, new Message("", "bob", new Date(), "Hallo", "token")));
        assertEquals(-1, StorageProviderMongoDB.addMessage(bob, new Message("", "", new Date(), "Hallo", "token")));
        assertEquals(-1, StorageProviderMongoDB.addMessage(bob, new Message("tom", "bob", null, "Hallo", "token")));
    }

    @Test
    public void getMessages() throws Exception {
        User bob = new User("bob");
        User tom = new User("tom");
        Message msg = new Message("tom", "bob", new Date(), "Hallo1", "token");
        assertEquals(1, StorageProviderMongoDB.addMessage(bob, msg));
        msg = new Message("tom", "bob", new Date(), "Hallo2", "token");
        assertEquals(2, StorageProviderMongoDB.addMessage(bob, msg));
        msg = new Message("tom", "bob", new Date(), "Hallo3", "token");
        assertEquals(3, StorageProviderMongoDB.addMessage(bob, msg));
        msg = new Message("tom", "bob", new Date(), "Hallo4", "token");
        assertEquals(4, StorageProviderMongoDB.addMessage(bob, msg));

        List<Message> msgs = StorageProviderMongoDB.getMessages(bob, 0);
        assertNotNull(msgs);
        assertEquals(4, msgs.size());
        assertEquals("Hallo4", msgs.get(3).text);
        assertEquals("Hallo1", msgs.get(0).text);
        assertEquals(1, msgs.get(0).sequence);
        assertEquals(2, msgs.get(1).sequence);

        assertNull(StorageProviderMongoDB.getMessages(bob, 5));

        msgs = StorageProviderMongoDB.getMessages(bob, 1);
        assertNotNull(msgs);
        assertEquals(2, msgs.size());
        assertEquals("Hallo4", msgs.get(1).text);
        assertEquals("Hallo3", msgs.get(0).text);
        assertEquals(3, msgs.get(0).sequence);
        assertEquals(4, msgs.get(1).sequence);

        msgs = StorageProviderMongoDB.getMessages(bob, 1);
        assertNotNull(msgs);
        assertEquals(2, msgs.size());
        assertEquals("Hallo4", msgs.get(1).text);
        assertEquals("Hallo3", msgs.get(0).text);
        assertEquals(3, msgs.get(0).sequence);
        assertEquals(4, msgs.get(1).sequence);

        // Users not yet in db return empty List
        msgs = StorageProviderMongoDB.getMessages(new User("MrNotInDB"), 0);
        assertNotNull(msgs);
        assertEquals(0, msgs.size());
    }

    @Test
    public void removeMessages() throws Exception {
        User bob = new User("bob");
        User tom = new User("tom");
        Message msg = new Message("tom", "bob", new Date(), "Hallo1", "token");
        assertEquals(1, StorageProviderMongoDB.addMessage(bob, msg));
        msg = new Message("tom", "bob", new Date(), "Hallo2", "token");
        assertEquals(2, StorageProviderMongoDB.addMessage(bob, msg));
        msg = new Message("tom", "bob", new Date(), "Hallo3", "token");
        assertEquals(3, StorageProviderMongoDB.addMessage(bob, msg));
        msg = new Message("tom", "bob", new Date(), "Hallo4", "token");
        assertEquals(4, StorageProviderMongoDB.addMessage(bob, msg));

        List<Message> msgs = StorageProviderMongoDB.getMessages(bob, 0);
        assertEquals(4, msgs.size());
        assertEquals("Hallo4", msgs.get(3).text);
        assertEquals("Hallo1", msgs.get(0).text);
        assertEquals(1, msgs.get(0).sequence);
        assertEquals(2, msgs.get(1).sequence);

        StorageProviderMongoDB.removeMessages(bob, 1);
        msgs = StorageProviderMongoDB.getMessages(bob, 0);
        assertEquals(3, msgs.size());
        assertEquals("Hallo4", msgs.get(1).text);
        assertEquals("Hallo3", msgs.get(0).text);
        assertEquals(3, msgs.get(0).sequence);
        assertEquals(4, msgs.get(1).sequence);
        StorageProviderMongoDB.removeMessages(bob, 3);
        msgs = StorageProviderMongoDB.getMessages(bob, 0);
        assertEquals(0, msgs.size());
    }

}