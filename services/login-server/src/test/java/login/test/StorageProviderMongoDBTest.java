package login.test;

import login.server.Config;
import login.server.Service;
import login.server.StorageProviderMongoDB;
import login.server.User;
import org.junit.Before;
import org.junit.Test;
import services.common.StorageException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

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

        StorageProviderMongoDB.clearForTest(new User[]{
                new User("tom@web.de", "abcd", "Tom"),
                new User("bob@web.de", "1234", "Bob")
        });
    }

    @Test
    public void retrieveUser() throws Exception {
        assertNotNull(StorageProviderMongoDB.retrieveUser("tom@web.de", "Tom"));
        assertNotNull(StorageProviderMongoDB.retrieveUser("tom@web.de", null));
        assertNull(StorageProviderMongoDB.retrieveUser("tom@web.de", "Bob"));
        assertNull(StorageProviderMongoDB.retrieveUser("bob@web.de", "Tom"));
        assertNull(StorageProviderMongoDB.retrieveUser("sdfsdg", "Tom"));
    }

    @Test
    public void saveToken() throws Exception {
        StorageProviderMongoDB.saveToken("hallo", "2025-01-01T09:13:37+0000", "Tom");
    }

    @Test
    public void retrieveToken() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(Service.ISO8601);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND,1);
        c.set(Calendar.MILLISECOND, 0);
        StorageProviderMongoDB.saveToken("hallo", sdf.format(c.getTime()), "Bob");
        assertEquals(c.getTime(), StorageProviderMongoDB.retrieveTokenExpireDate("Bob", "hallo"));
        // Wait for token to expire
        Thread.sleep(1000);
        assertNull(StorageProviderMongoDB.retrieveTokenExpireDate("Bob", "hallo"));
    }
}