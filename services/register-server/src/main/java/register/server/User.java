package register.server;

import services.common.SecurityHelper;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

/**
 * Immutable class that represents a user in the user base.
 */
public class User {
    private String pseudonym;
    private String securePassword;
    private String email;
    private List<String> contacts;

    /**
     * Creates a new user a hashes the given clear text password.
     *
     * @param pseudonym The users chosen pseudonym.
     * @param password  The users clear text password.
     * @param email     The users registration email address.
     */
    public User(String pseudonym, String password, String email) {
        this.pseudonym = pseudonym;
        try {
            this.securePassword = SecurityHelper.hashPassword(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            System.out.println("Someting went wrong when hashing a password!");
        }
        this.email = email;
        this.contacts = new ArrayList<>();
    }

    /**
     * Creates a new user from an existing data source. Used for database interfacing.
     *
     * @param pseudonym      The users pseudonym.
     * @param securePassword The users password in hashed form. See SecurityHelper.
     * @param email          The users registration email address.
     * @param contacts       This users contact list.
     */
    public User(String pseudonym, String securePassword, String email, List<String> contacts) {
        this.pseudonym = pseudonym;
        this.securePassword = securePassword;
        this.email = email;
        this.contacts = new ArrayList<>();
        this.contacts.addAll(contacts);
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public String getHashedPassword() {
        return securePassword;
    }

    /**
     * Adds a new contact to the users contact list and returns the users new profile.
     *
     * @param contact The contact to add to the contact list.
     * @return Returns this user new state or null if the contact could not be added to the list.
     */
    public User addContact(User contact) {
		if (!StorageProviderMongoDB.newContact(this,contact.getPseudonym())) {
		    return null;
        } else {
            ArrayList<String> contacts = new ArrayList<>();
            contacts.addAll(this.contacts);
            contacts.add(contact.pseudonym);
            return new User(this.pseudonym, this.securePassword, this.email, contacts);
        }
    }
}
