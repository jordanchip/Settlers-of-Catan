package shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import shared.exceptions.SchemaMismatchException;

/** An immutable representation of a chat message
 * @author beefster
 *
 */
public class MessageList 
implements Serializable {
	private static final long serialVersionUID = 8846728195965070930L;
	
	private List<String> message;
	private List<String> source;

	public MessageList() {
		message = new ArrayList<String>();
		source = new ArrayList<String>();
	}
	
	@SuppressWarnings("unchecked")
	public MessageList(JSONObject json) throws SchemaMismatchException {
		//JSONObject chat = (JSONObject) json.get("chat");
		try {
			if (json.containsKey("lines")) {
				message = new ArrayList<String>();
				source = new ArrayList<String>();
				for (Object obj : (List<Object>) json.get("lines")) {
					message.add((String) ((JSONObject)obj).get("message"));
					source.add((String) ((JSONObject)obj).get("source"));
				}
			}
			else {
				message = new ArrayList<String>();
				source = new ArrayList<String>();
				for (Object obj : (List<Object>)json.get("source")) {
					source.add((String)obj);
				}
				for (Object obj : (List<Object>)json.get("message")) {
					message.add((String)obj);
				}
			}
		} catch (ClassCastException | IllegalArgumentException e) {
			e.printStackTrace();
			throw new SchemaMismatchException("The JSON does not follow the expected schema " +
					"for an EdgeObject:\n" + json.toJSONString());
		}
	}

	/**
	 * @return the message
	 */
	public List<String> getMessage() {
		return message;
	}

	/**
	 * @return the source
	 */
	public List<String> getSource() {
		return source;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageList other = (MessageList) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}
	
	public void add(String source, String message) {
		this.source.add(source);
		this.message.add(message);
	}
	
}
