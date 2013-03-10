package data;

import java.util.Vector;

public class User {
	private long id;
	private String username;
	private String key;
	private Vector<Long> channels;
	private int color;
	private boolean italic, bold, online;
	
	public User(long newId, String newUsername, String newKey){
		id = newId;
		username = newUsername;
		key = newKey;
		channels = new Vector<Long>();
	}
	
	public void addChannel(long newChannelId){
		channels.add(newChannelId);
	}
	
	public Vector<Long> getChannels() {
		return channels;
	}
	
	public int getColor() {
		return color;
	}
	public long getId() {
		return id;
	}

	public String getKey(){
		return key;
	}
	public String getUsername() {
		return username;
	}

	public boolean isBold() {
		return bold;
	}
	public boolean isItalic() {
		return italic;
	}
	public boolean isOnline() {
		return online;
	}
	public void removeChannel(long lvChannelId){
		channels.remove(lvChannelId);
	}
	

	public void setBold(boolean bold) {
		this.bold = bold;
	}
	public void setChannels(Vector<Long> channels) {
		this.channels = channels;
	}
	

	public void setColor(int color) {
		this.color = color;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public void setItalic(boolean italic) {
		this.italic = italic;
	}
	public void setKey(String newKey){
		key = newKey;
	}
	
	public void setOnline(boolean online) {
		this.online = online;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
}
