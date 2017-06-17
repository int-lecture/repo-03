package register.test;

import org.junit.Before;
import org.junit.Test;
import register.server.Config;
import register.server.StorageProviderMongoDB;
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
    }

    @Test
    public void createNewUser() throws Exception {
    }

    @Test
    public void getUserProfile() throws Exception {
    }

    @Test
    public void userExists() throws Exception {
    }

}