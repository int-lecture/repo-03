package login.test;

import login.server.Config;
import login.server.StorageProviderMongoDB;
import login.server.User;
import org.junit.Before;
import org.junit.Test;
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

        StorageProviderMongoDB.clearForTest(new User[]{
                new User("tom@web.de","abcd","Tom"),
                new User("bob@web.de","1234","Bob")
        });
    }

    @Test
    public void retrieveUser() throws Exception {
        fail();
    }

    @Test
    public void saveToken() throws Exception {
        fail();
    }

    @Test
    public void retrieveToken() throws Exception {
        fail();
    }

    @Test
    public void deleteToken() throws Exception {
        fail();
    }
}