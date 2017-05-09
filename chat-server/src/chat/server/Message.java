package chat.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Chat message.
 */
public class Message {

	/** From. */
	String from;

	/** To. */
	String to;

	/** Date. */
	Date date;

	/** Text. */
	String text;

	/** Sequence number. */
	int sequence;

	/** Token. */
	String token;

	/**
	 * Create a new message.
	 *
	 * @param from
	 *            From.
	 * @param to
	 *            To.
	 * @param date
	 *            Date.
	 * @param text
	 *            Contents.
	 * @param sequence
	 *            Sequence-Number.
	 */
	public Message(String from, String to, Date date, String text, String token, int sequence) {
		this.from = from;
		this.to = to;
		this.date = date;
		this.text = text;
		this.token = token;
		this.sequence = sequence;
	}

	/**
	 * Create a new message.
	 *
	 * @param from
	 *            From.
	 * @param to
	 *            To.
	 * @param date
	 *            Date.
	 * @param text
	 *            Contents.
	 */
	public Message(String from, String to, Date date, String text, String token) {
		this(from, to, date, text, token, 0);
	}

	/**
	 * Creates a new message from a JSON object string.
	 *
	 * @param jsonSource
	 *            JSON object string source
	 * @throws ParseException
	 *             String was not a valid JSON Message object.
	 */
	public static Message fromJson(String jsonSource) throws ParseException {
		try {
			JSONObject obj = new JSONObject(jsonSource);
			Date date = null;
			if (obj.has("date")) {
				SimpleDateFormat sdf = new SimpleDateFormat(Service.ISO8601);
				date = sdf.parse(obj.getString("date"));
			}

			return new Message(obj.getString("from"), obj.getString("to"), date, obj.getString("text"), obj.getString("token"),
					obj.optInt("sequence"));
		} catch (JSONException ex) {
			throw new ParseException("String was not a valid JSON Message object.", -1);
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat(Service.ISO8601);

		return String.format("{ 'from': '%s', 'to': '%s', 'date': '%s', 'text': '%s', 'token': '%s'}".replace('\'', '"'), from, to,
				sdf.format(new Date()), text, token);
	}

	/**
	 * Converts the message to a JSON object.
	 * @param isRecvConfirmation If true only date and sequence number are added to the JSON object.
	 * @return Returns a JSON object representing the message.
	 * @throws JSONException Somehow the message could not be converted.
	 */
	public JSONObject toJson(boolean isRecvConfirmation) throws JSONException {
		JSONObject obj = new JSONObject();
		SimpleDateFormat sdf = new SimpleDateFormat(Service.ISO8601);
		obj.put("date", sdf.format(date));
		obj.put("sequence", sequence);

		// The servers confirms a received client message by returning the messages date and sequence number.
		if (!isRecvConfirmation) {
			obj.put("from", from);
			obj.put("to", to);
			obj.put("text", text);
			obj.put("token", token);
		}

		return obj;
	}
}
