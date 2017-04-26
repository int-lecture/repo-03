package chat.server;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * A chat user. Contains the user's name and information about his messages.
 *
 */
public class User {

	private static final boolean removeOldMessages = true;

	/**
	 * The username.
	 */
	private String name;

	/**
	 * The sequence number of the last message sent.
	 */
	private int sequenceNumber;

	/**
	 * Messages to be delievered.
	 */
	private Queue<Message> messages = new ArrayDeque<Message>();

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
		// Fetch all new messages without deleting them.
		for (Message message : recvMsgs) {
			recvMsgs.add(message);
		}

		return recvMsgs;
	}
}
