package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import run.Controller;

import com.google.gson.Gson;

import data.Channel;
import data.Message;
import data.Request;


public class RequestManager {
	private class ClientInputThread extends Thread{
		private Socket clientSocket;
		private InputStream is;
		private DataInputStream dis;
		private String key, username;
		private Gson gson;
		private boolean running;
		
		public ClientInputThread(Socket newClientSocket, String key){
			clientSocket = newClientSocket;
			this.key = key;
			gson = new Gson();
			running = true;
			username = "";
			try {
				is = clientSocket.getInputStream();
				dis = new DataInputStream(is);
				System.out.println("Request Service - New Client thread listener.");
			} catch (IOException e) {
				System.out.println("Request Service Error - IOException while getting the InputStream from the client socket : " + key);
				e.printStackTrace();
			}
		}
		
		public void read(String readBuffer){
			Request request = gson.fromJson(readBuffer, Request.class);
			
			switch(request.getType()){
				case "message" : 
					readMessage(request);
				break;
				
				case "login" :
					readLogin(request);
				break;
				
				case "password" :
					readPassword(request);
				break;
				
				case "logout" :
					readLogout(request);
				break;
				
				case "joinned" :
					readJoinned(request);
				break;
				
				case "left" : 
					readLeft(request);
				break;
				
				case "goodbye":
					readGoodbye();
				break;
				
				default : System.out.println("Request Service Error - Unkown message type :" + request.getType() + "\nfrom : " + key);
			}
		}
		
		private void readLogin(Request request){
			username = request.getContent();
			if(ctrl.usedUsername(username)){
				System.out.println("Username used!");
				username = "";
				ctrl.send(new Request("login","used",key));
			}
			else if(ctrl.isBan(key.split(":")[0])){
				username = "";
				ctrl.send(new Request("login","banned",key));
				ctrl.removeClientByKey(key);
			}
			else{
				if(ctrl.isAdmin(username)){
					ctrl.send(new Request("login","password",key));
				}
				else{
					ctrl.login(username,key);
					ctrl.send(new Request("login","success" + "&" + gson.toJson(ctrl.getUser(username)),key));
				}
			}
		}
		
		private void readMessage(Request request){
			Message message = gson.fromJson(request.getContent(), Message.class);
			
			GregorianCalendar cal = new GregorianCalendar();
			SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			df.setLenient(false);
			message.setDate(df.format(cal));
			
			Channel channel = (Channel)ctrl.getChannel(message.getChannel());
			for(String username : channel.getUsers()){
				message.addKey(ctrl.getUser(username).getKey());
			}
			ctrl.newMessage(message.getChannel(),message);
			ctrl.send(request);
		}
		
		private void readPassword(Request request){
			String password = request.getContent();
			if(ctrl.checkPassword(username, password, key)){
				ctrl.login(username,key);
				ctrl.send(new Request("password","success",key));
			}
			else{
				username = "";
				ctrl.send(new Request("password","fail",key));
			}
		}
		
		private void readLogout(Request request){
			ctrl.logoutUser(username);
			username = "";
			sendRequest(new Request("welcome",ctrl.getServerName(),key));
		}
		
		private void readJoinned(Request request){
			try{
				long chanId = Long.parseLong(request.getContent());
				ctrl.joinChannel(username, chanId);
			}catch(NumberFormatException e){
				System.out.println("Request Service Error - Join ChannelId NaN :" + request.getContent() + "\nfrom : " + key);
			}
		}
		
		private void readLeft(Request request){
			try{
				long chanId = Long.parseLong(request.getContent());
				ctrl.leaveChannel(username, chanId);
			}catch(NumberFormatException e){
				System.out.println("Request Service Error - Leave ChannelId NaN :" + request.getContent() + "\nfrom : " + key);
			}
		}
		
		private void readGoodbye(){
			turnOff();
			ctrl.disconnectClient(username, key);
			username = "";
			System.out.println("Request Service - A client disconnected.");
		}
		
		@Override
		public void run(){
			String readBuffer = "";
			while(running){
				try {
					if(dis.available()!= 0){
						readBuffer = dis.readUTF();
						read(readBuffer);
					}
				} catch (IOException e) {
					System.out.println("Request Service Error - IOException while reading the buffer of : " + key + "\nRead : "+ readBuffer);
					e.printStackTrace();
				}
			}
		}
		
		public void turnOff(){
			running = false;
		}
		
		public void turnOn(){
			running = true;
		}
	}
	private class ClientOutputThread extends Thread{
		private HashMap<String, OutputStream> os;
		private HashMap<String, DataOutputStream> dos;
		private LinkedList<Request> stack;
		private boolean running;
		
		public ClientOutputThread(){
			running = true;
			os = new HashMap<String, OutputStream>();
			dos = new HashMap<String, DataOutputStream>();
			stack = new LinkedList<Request>();
		}
		
		public void addClient(Socket clientSocket , String key){
			try {
				os.put(key, clientSocket.getOutputStream());
				dos.put(key, new DataOutputStream(os.get(key)));
			} catch (IOException e) {
				System.out.println("Request Service Error - IOException while getting the OutputStream from the client socket : " + key);
				e.printStackTrace();
			}
		}
		
		public void removeClient(String key){
			os.remove(key);
			dos.remove(key);
		}
		
		@Override
		public void run(){
			while(running){
				if(stack.isEmpty())
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						System.out.println("Request Service Error - OutputThread was interrupted while sleeping.");
						e.printStackTrace();
					}
				else{
					synchronized(stack){
						send(stack.pop());
					}
				}
			}
		}
		
		private void send(Request request){
			Gson gson = new Gson();
			for(String key : request.getKeys()){
				try {
					dos.get(key).writeUTF(gson.toJson(request));
				} catch (IOException e) {
					System.out.println("Request Service Error - IOException while sending data to the client socket : " + key + "\nRequest : " + gson.toJson(request));
					e.printStackTrace();
				}
			}
		}
		
		public void stackRequest(Request request){
			synchronized(stack){
				stack.add(request);
			}
		}
		
		public void turnOff(){
			running = false;
		}
		
		public void turnOn(){
			running = true;
		}
		
		public boolean isStacked(){
			return !stack.isEmpty();
		}
	}
	
	private static RequestManager manager;
	private HashMap<String, ClientInputThread> inputThreads;
	private ClientOutputThread outputThread;
	private Controller ctrl;
	
	
	public static RequestManager getInstance(Controller newCtrl){
		if(manager != null)
			return manager;
		else{
			manager = new RequestManager(newCtrl);
			return manager;
		}
	}
	
	public RequestManager(Controller newCtrl){
		ctrl = newCtrl;
		inputThreads = new HashMap<String, ClientInputThread>();
		outputThread = new ClientOutputThread();
		outputThread.start();
		System.out.println("Request Service - Initialized.");
	}
	
	public void newClientThread(Socket clientSocket, String key){
		inputThreads.put(key,new ClientInputThread(clientSocket, key));
		inputThreads.get(key).start();
		outputThread.addClient(clientSocket, key);
		sendRequest(new Request("welcome",ctrl.getServerName(),key));
	}
	
	public void removeClient(String key){
		outputThread.removeClient(key);
		inputThreads.get(key).turnOff();
		inputThreads.get(key).interrupt();
		inputThreads.remove(key);
	}
	
	public void sendRequest(Request request){
		outputThread.stackRequest(request);
	}
	
	public void disable(){
		outputThread.turnOff();
		for(String key : inputThreads.keySet())
			inputThreads.get(key).turnOff();
	}
	
	public void enable(){
		outputThread.turnOn();
		for(String key : inputThreads.keySet())
			inputThreads.get(key).turnOn();
	}
	
	public void notifyShutdown(String reason){
		for(String key : inputThreads.keySet()){
			inputThreads.get(key).turnOff();
			inputThreads.get(key).interrupt();
			inputThreads.remove(key);
			sendRequest(new Request("shutdown",reason,key));
		}
	}
	
	public void shutdown(){
		outputThread.turnOff();
		outputThread.interrupt();
	}
	
	public boolean isStacked(){
		return outputThread.isStacked();
	}
	
	public void sendChannels(String key){
		sendRequest(new Request("channels",new Gson().toJson(ctrl.getChannels()),key));
	}
	
	public void sendJoinned(String username, long channelId){
		Vector<String> keys = new Vector<String>();
		for(String user : ((Channel)ctrl.getChannel(channelId)).getUsers()){
			keys.add(ctrl.getUser(user).getKey());
		}
		sendRequest(new Request("joinned",username+"&"+channelId,keys));
	}
	
	public void sendLeft(String username, long channelId){
		Vector<String> keys = new Vector<String>();
		for(String user : ((Channel)ctrl.getChannel(channelId)).getUsers()){
			keys.add(ctrl.getUser(user).getKey());
		}
		sendRequest(new Request("left",username+"&"+channelId,keys));
	}
	
	public void sendWelcome(String key){
		new Request("welcome",ctrl.getServerName(),key);
	}
}
