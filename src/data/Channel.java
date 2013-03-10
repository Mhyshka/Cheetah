package data;

import java.util.Vector;

public class Channel extends ChannelTree{
	private Vector<String> users;
	private Vector<Message> messages;
	private String password;
	private boolean whisper;
	private User owner;
	private Vector<String> moderators;
	
	
	public Channel(long id, String name, ChannelGroup parent, boolean isWhisper){
		setName(name);
		setId(id);
		setParent(parent);
		whisper = isWhisper;
		users = new Vector<String>();
		moderators = new Vector<String>();
		messages = new Vector<Message>();
	}
	
	public void addMessage(Message message){
		synchronized(messages){
			this.messages.add(message);
		}
	}
	public void addModerator(String newModeratorName){
		this.moderators.add(newModeratorName);
	}
	public void addUser(String newUsername){
		synchronized(users){
			this.users.add(newUsername);
		}
	}
	public Vector<Message> getMessages() {
		synchronized(messages){
			return messages;
		}
	}
	
	public Vector<String> getModerators() {
		return moderators;
	}
	public User getOwner() {
		return owner;
	}
	public String getPassword() {
		return password;
	}
	public Vector<String> getUsers() {
		synchronized(users){
			return users;
		}
	}
	public boolean isWhisper() {
		return whisper;
	}
	
	public void removeMessage(int index){
		synchronized(messages){
			this.messages.removeElementAt(index);
		}
	}
	public void removeMessage(Message rmMessage){
		synchronized(messages){
			this.messages.remove(rmMessage);
		}
	}
	
	public void removeModerator(String rmModeratorName){
		this.moderators.remove(rmModeratorName);
	}
	public void removeUser(String rmUsername){
		synchronized(users){
			this.users.remove(rmUsername);
		}
	}
	
	public void setMessages(Vector<Message> messages) {
		synchronized(messages){
			this.messages = messages;
		}
	}
	public void setModerators(Vector<String> moderators) {
		this.moderators = moderators;
	}
	
	public void setOwner(User owner) {
		this.owner = owner;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setUsers(Vector<String> usernames) {
		synchronized(users){
			this.users = usernames;
		}
	}
	public void setWhisper(boolean whisper) {
		this.whisper = whisper;
	}
	
}
