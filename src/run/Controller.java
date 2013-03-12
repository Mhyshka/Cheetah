package run;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;

import network.ConnectionManager;
import network.RequestManager;
import data.Channel;
import data.ChannelTree;
import data.Message;
import data.Request;
import data.User;


public class Controller {
	public enum SERVICE{
		CONNECTION_SERVICE, CHANNEL_SERVICE, REQUEST_SERVICE, USER_SERVICE;
	}
	
	private int port;
	private boolean running = false;
	private ConnectionManager connectionManager;
	private ChannelManager channelManager;
	private RequestManager requestManager;
	private UserManager userManager;
	private Console console;
	private String serverName;
	
	
	public static void start(int port) {	
		new Controller(port);
	}

	private Controller(int port){
		this.port = port;
		serverName = "Cheetah Server";
		running = true;
		connectionManager = ConnectionManager.getInstance(this);
		requestManager = RequestManager.getInstance(this);
		channelManager = ChannelManager.getInstance(this);
		userManager = UserManager.getInstance(this);
		
		console = Console.getInstance(this);
		console.start();
	}
	
	public String getServerName(){
		return serverName;
	}
	
	public void addAdmin(String username, String key){
		newAdmin(username, key);
	}

	public void addUser(String username, String key){
		login(username, key);
	}
	
	public void banUser(String username){
		userManager.banUser(username);
	}
	
	public void banUserByKey(String key){
		userManager.banUserByKey(key);
	}

	public boolean checkPassword(String username, String password, String key) {
		return userManager.checkPassword(username,password,key);
	}

	public void closeSocket(String username){
		connectionManager.closeSocket(getUser(username).getKey());
	}
	
	public void closeSocketByKey(String key){
		connectionManager.closeSocket(key);
	}

	public ChannelTree getChannel(long chanId){
		return channelManager.getChannel(chanId);
	}

	public int getPort(){
		return port;
	}
	
	public ServerSocket getServerSocket(){
		return connectionManager.getServerSocket();
	}
	
	public User getUser(String username){
		return userManager.getUser(username);
	}

	public Socket getUserSocket(String username){
		return connectionManager.getSocket(getUser(username).getKey());
	}

	public Socket getUserSocketByKey(String key){
		return connectionManager.getSocket(key);
	}
	
	public boolean isAdmin(String username) {
		return userManager.isAdmin(username);
	}
	
	public boolean isBan(String key){
		return userManager.isBan(key);
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void joinChannel(String username, long channelId){
		userManager.linkUserToChannel(username, channelId);
	}
	
	public void kick(String pseudo){
		// TODO
	}
	
	public void leaveChannel(String username, long channelId){
		userManager.unlinkUserToChannel(username, channelId);
	}
	
	public void listChannels(long id){
		channelManager.listChannels(id, "");
	}
	
	public void listCommands(){
		console.listCommands();
	}
	
	public void listUsers(long chanId){
		userManager.listUsers(chanId);
	}
	
	public void listWhispers(){
		ChannelTree tmp;
		Channel chan;
		String str;
		for(long id : channelManager.getTree().keySet()){
			tmp = channelManager.getChannel(id);
			if(tmp instanceof Channel){
				str = "";
				if(((Channel) tmp).isWhisper()){
					chan = (Channel)tmp;
					str += " " + id + " - " + chan.getName() + " - owner : " + chan.getOwner().getUsername() +" - users : ";
					for(String username : chan.getUsers()){
						str += "\n" + getUser(username).getId()+ " - "+getUser(username);
					}
					System.out.println(str);
				}
			}
		}
	}
	
	public void login(String username, String key){
		userManager.addUser(username, key);
		sendChannels(key);
	}
	
	public void disconnectClient(String username, String key){
		if(!username.isEmpty())
			removeUser(username, true);
		requestManager.removeClient(key);
		connectionManager.closeSocket(key);
	}
	
	public void logoutUser(String username) {
		userManager.logoutUser(username);
	}
	
	public void newAdmin(String username, String password){
		userManager.addAdmin(username, password);
	}
	
	public long newChannel(String name, long parentId, Boolean isWhisper){
		return channelManager.newChannel(name,parentId,isWhisper);
	}
	
	public long newChannelGroup(String name, long parentId){
		return channelManager.newChannelGroup(name, parentId);
	}
	
	public void newMessage(long channelId, Message message){
		((Channel)getChannel(channelId)).addMessage(message);
	}

	public void removeAdmin(String username){
		userManager.removeAdmin(username);
	}
	
	public void removeChannel(long channelId){
		channelManager.removeChannel(channelId);
	}
	
	public void removeChannelGroup(long channelId, boolean lenient){
		channelManager.removeChannelGroup(channelId, lenient);
	}
	
	public void removeClient(String username){
		requestManager.removeClient(getUser(username).getKey());
		connectionManager.closeSocket(getUser(username).getKey());
	}

	public void newClient(Socket socket, String key){
		requestManager.newClientThread(socket, key);
	}
	
	public void removeClientByKey(String key){
		requestManager.removeClient(key);
		connectionManager.closeSocket(key);
	}

	public void removeUser(long userId){
		userManager.removeUser(userId);
	}
	
	public void removeUser(String username, boolean disconnecting){
		userManager.removeUser(username, disconnecting);
	}
	
	public void send(Request request){
		requestManager.sendRequest(request);
	}
	
	public void sendChannels(){
		requestManager.sendChannels();
	}
	
	public void sendNewChannel(Channel newChannel){
		requestManager.sendNewChannel(newChannel);
	}
	
	public void sendRmChannel(long channelId){
		requestManager.sendRmChannel(channelId);
	}
	
	public HashMap<Long,ChannelTree> getChannels(){
		return channelManager.getChannels();
	}
	
	public void shutdown(String reason){
		System.out.println("Logging out users.");
		userManager.shutdown();
		System.out.println("Notifying users.");
		requestManager.notifyShutdown(reason);
		System.out.println("Closing connections.");
		while(requestManager.isStacked()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		connectionManager.shutdown();
		
		System.out.println("Saving Channels.");
		//TODO Save
		System.out.println("Server shuting down.");
		System.exit(0);
	}
	
	public void unbanUser(String username){
		userManager.unbanUser(username);
	}
	
	public void unbanUserByKey(String key){
		userManager.unbanUserByKey(key);
	}

	public boolean usedUsername(String username) {
		return userManager.usedUsername(username);
	}
	
	public void sendChannels(String key){
		requestManager.sendChannels(key);
	}
	
	public void sendJoinned(String username, long channelId){
		requestManager.sendJoinned(username, channelId);
	}
	
	public void sendLeft(String username, long channelId){
		requestManager.sendLeft(username, channelId);
	}
	
	public void sendWelcome(String key){
		requestManager.sendWelcome(key);
	}
	
	public HashMap<String, User> getUsers(){
		return userManager.getUsers();
	}
	
	public Vector<String> getKeys(){
		return userManager.getKeys();
	}
	
	public boolean isInit(SERVICE service){
		boolean isInit = false;
		switch(service){
			case CONNECTION_SERVICE :
				if(connectionManager != null)
					isInit = true;
			break;

			case CHANNEL_SERVICE :
				if(channelManager != null)
					isInit = true;
			break;
			
			case REQUEST_SERVICE :
				if(requestManager != null)
					isInit = true;
			break;
			
			case USER_SERVICE :
				if(userManager != null)
					isInit = true;
			break;
				
			default : isInit = false;;
		}
		return isInit;
	}
}
