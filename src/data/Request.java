package data;

import java.util.Vector;

public class Request {
	Vector<String> keys;
	String content;
	String type;
	
	public Request(String newType, String newContent, String newKey){
		type = newType;
		content = newContent;
		keys = new Vector<String>();
		keys.add(newKey);
	}
	
	public Request(String newType, String newContent, Vector<String> newKeys){
		type = newType;
		content = newContent;
		keys = newKeys;
	}
	
	public void addKey(String key){
		keys.add(key);
	}

	public String getContent() {
		return content;
	}

	public Vector<String> getKeys() {
		return keys;
	}

	public String getType() {
		return type;
	}

	public void removeKey(String rmKey){
		keys.remove(rmKey);
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setKeys(Vector<String> keys) {
		this.keys = keys;
	}
	
	public void setType(String type) {
		this.type = type;
	}
}
