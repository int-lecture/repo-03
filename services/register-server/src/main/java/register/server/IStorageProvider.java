package register.server;

public interface IStorageProvider {
	boolean createNewUser(User user);
	User getUserProfile(String name);
	boolean userExists(String name,String email);
}
