package register.server;

public interface IStorageProvider {
	public boolean createNewUser(User user);
	public User getUserProfile(String name);
	public boolean userExists(String name,String email);
}
