package register;

import java.io.Serializable;
import java.util.Collection;

public class RequestMessage implements Serializable {
	
	private String senderId;
	public String getSenderId() {
		return senderId;
	}
	
	private Object[] keys;
	public Object[] getKeys() {
		return keys;
	}
	
	public RequestMessage(String senderId, Collection<Object> keys) {
		this.senderId = senderId;
		this.keys = keys.toArray();
	}
}
