package network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import run.Controller;

public class ConnectionManager {
	private class ConnectionListener extends Thread{
		@Override
		public void run() {
			Socket clientSocket = null;
			try{
				clientSocket = new Socket();
				clientSocket = servSocket.accept();
			}
			catch(IOException e){
				System.out.println("Connection Service Error : IOexception while waiting for a client.");
				e.printStackTrace();
			}
			catch(SecurityException e){
				System.out.println("Connection Service Error : Unallowed operation while waiting for a client. Check your firewall.");
				e.printStackTrace();
			}
			
			if(clientSocket.isConnected()){
				String key = clientSocket.getInetAddress()+":"+clientSocket.getPort();
				key = key.substring(1);	
				addSocket(key, clientSocket);
				ctrl.newClient(clientSocket,key);
			}
			newThread();
		}
	}
	
	
	private ServerSocket servSocket;
	private HashMap<String,Socket> sockets;
	private static ConnectionManager manager;
	private ConnectionListener thread;
	private Controller ctrl;
	
	
	public static ConnectionManager getInstance(Controller ctrl){
		if(manager != null){
			return manager;
		}
		else{
			manager = new ConnectionManager(ctrl);
			return manager;
		}
	}

	
	private ConnectionManager(Controller ctrl){
		this.ctrl = ctrl;
		sockets = new HashMap<String,Socket>();
		try{
			servSocket = new ServerSocket(ctrl.getPort(),50,InetAddress.getLocalHost());
			System.out.println(servSocket.getLocalSocketAddress());
		}
		catch(IOException e){
				System.out.println("Connection Service Error : IOexception while creating the server's socket.");
				e.printStackTrace();
		}
		catch(IllegalArgumentException e){
			System.out.println("Connection Service Error : Illegal port while creating the server's socket.");
			e.printStackTrace();
		}
		catch(SecurityException e){
			System.out.println("Connection Service Error : Unallowed operation while creating the server's socket. Check your firewall.");
			e.printStackTrace();
		}
		thread = new ConnectionListener();
		newThread();
		System.out.println("Connection Service - Initialized.");
		if(!this.ctrl.isEnabled(Controller.SERVICE.CHANNEL_SERVICE))
			System.out.println("Connection Service - is stopped.");
		else
			System.out.println("Connection Service - is running.");
	}
	
	public void addSocket(String key, Socket newSocket){
		if(!this.ctrl.isEnabled(Controller.SERVICE.CHANNEL_SERVICE))
			System.out.println("Connection service - is stopped. Can't add a new Client.");
		else{
			System.out.println("Connection Service - Client added : " + key);
			sockets.put(key, newSocket);
		}
	}
	
	public void closeSocket(String key){
		try {
			sockets.get(key).close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sockets.remove(key);
	}
	
	public void disable(){
		try{
			thread.interrupt();
		}
		catch(SecurityException e){
			System.out.println("Connection Service - Couldn't stop the actual running thread.");
		}
	}
	
	public void enable(){
		thread.run();
	}
	
	public ServerSocket getServerSocket(){
		return servSocket;
	}

	public Socket getSocket(String key){
		return sockets.get(key);
	}
	
	private void newThread(){
		if(!this.ctrl.isEnabled(Controller.SERVICE.CHANNEL_SERVICE))
			System.out.println("Connection service - is stopped. Can't run a new Thread to wait for a client.");
		else{
			thread = new ConnectionListener();
			thread.start();
		}
	}
	
	public void shutdown(){
		for(String key : sockets.keySet()){
			closeSocket(key);
		}
	}
}
