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
	public Message(String from, String to, Date date, String text, int sequence) {
		this.from = from;
		this.to = to;
		this.date = date;
		this.text = text;
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
	public Message(String from, String to, Date date, String text) {
		this(from, to, date, text, 0);
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

			return new Message(obj.optString("from"), obj.optString("to"), date, obj.optString("text"),
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

		return String.format("{ 'from': '%s', 'to': '%s', 'date': '%s', 'text': '%s'}".replace('\'', '"'), from, to,
				sdf.format(new Date()), text);
	}

	public String toResponse() {
		SimpleDateFormat sdf = new SimpleDateFormat(Service.ISO8601);

		return String.format("{'date': '%s', 'sequence': '%d'}".replace('\'', '"'), sdf.format(new Date()), sequence);
	}
}
