package chat.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;

import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;

/**
 * A chat user. Contains the user's name and information about his messages.
 */
public class User {

	private static final boolean removeOldMessages = true;

	private static final String url = "http://localhost:5001";
	/**
	 * The user's name.
	 */
	private String name;

	/**
	 * The sequence number of the last message sent.
	 */
	private int sequenceNumber;

	/**
	 * Messages to be delivered.
	 */
	private Queue<Message> messages = new ArrayDeque<Message>();

	/**
	 * The user´s token.
	 */
	private String token;

	/**
	 * The expiration date of the token.
	 */
	private Date expireDate;

	/**
	 * Creates a new user with the given name.
	 *
	 * @param name
	 *            The user's name.
	 */
	public User(String name) {
		this.name = name;
	}

	/**
	 * Sends a message to the user. The method returns a message object with the
	 * correct sequence number.
	 *
	 * @param msg
	 *            The message to sent to the user.
	 * @return The sent message with the correct sequence number.
	 */
	public Message sendMessage(Message msg) {
			msg.sequence = sequenceNumber++;
			messages.add(msg);
			System.out.println(String.format("%s -> %s [%d]: %s", msg.from, msg.to, msg.sequence, msg.text));
			return msg;
	}

	/**
	 * Gets all received message with a sequence number lower than the
	 * parameter. Returned messages are deleted.
	 *
	 * @param sequenceNumber
	 *            The last sequence number received by the client or 0 to fetch
	 *            all available messages.
	 * @return Returns all message with a sequence number lower than the
	 *         parameter.
	 */
	public List<Message> receiveMessages(int sequenceNumber) {
		ArrayList<Message> recvMsgs = new ArrayList<>();

		for (Message message : messages) {
			if (sequenceNumber == 0 || message.sequence > sequenceNumber) {
				recvMsgs.add(message);
			}
		}

		// Remove all message with a sequence <= the parameter. This removes all
		// messages from storage that
		// the client confirmed as received.
		if (User.removeOldMessages) {
			while (!this.messages.isEmpty()) {
				Message msg = this.messages.peek();
				if (msg.sequence <= sequenceNumber) {
					this.messages.poll();
				} else {
					break;
				}
			}
		}

		return recvMsgs;
	}

	/**
	 * Method to authenticate a user with his token.
	 *
	 * @return Returns true if the authentication was successfull and false if
	 *         not
	 */
	public boolean authenticateUser(String token) {
		SimpleDateFormat sdf = new SimpleDateFormat(Service.ISO8601);
		System.out.println(token);
		if (this.token == token) {
			if (sdf.format(new Date()).compareTo(expireDate.toString()) < 0) {
				return true;
			}
		}

		JSONObject obj = new JSONObject();
		try {
			obj.put("token", token);
			obj.put("pseudonym", name);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Client client = Client.create();
		String response = client.resource(url + "/auth").accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON).post(String.class, obj.toString());
		client.destroy();

		try {
			JSONObject jo = new JSONObject(response);
			if (jo.get("success").equals("true")) {
				this.expireDate = sdf.parse(jo.getString("expire-date"));
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
}
