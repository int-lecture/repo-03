package login.server;

import java.util.Date;

public class User {
	public String email;
	public String password;
	public String currentToken;
	public String pseudonym;
	public Date tokenExpiration;
}
