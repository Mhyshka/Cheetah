package run;

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import data.Channel;
import data.ChannelGroup;
import data.ChannelTree;
import data.User;

public class UserManager {
	public static UserManager manager;
	private long userId;
	private Controller ctrl;
	private HashMap<String, User> users;
	private HashMap<String, String[]> admins;
	private HashMap<String, GregorianCalendar> banList;
	
	
	public static UserManager getInstance(Controller newCtrl){
		if(manager != null)
			return manager;
		else{
			manager = new UserManager(newCtrl);
			return manager;
		}
	}
	
	public UserManager(Controller newCtrl){
		ctrl = newCtrl;
		users = new HashMap<String, User>();
		admins = new HashMap<String, String[]>();
		banList = new HashMap<String, GregorianCalendar>();
		userId = 0;
	}

	public void addAdmin(String username, String password){
		if(admins.containsKey(username))
			System.out.println("User Service Warning -" +
					" This username was already set for an admin." +
					" His password has been modified.");
		else
			System.out.println("User Service - Admin created.");
		String infos[] = {password, null};
		admins.put(username, infos);
	}
	
	public void addUser(String username, String key){
		users.put(username, new User(getUserId(), username, key));
		nextUserId();
		if(admins.containsKey(username))
			admins.get(username)[1] = key;
		System.out.println("User Service - User : " + username + " logged in.");
	}
	
	public void banUser(String username){
		banUserByKey(getUser(username).getKey());
	}
	
	public void banUserByKey(String key){
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(GregorianCalendar.DAY_OF_MONTH, 1);
		banList.put(key, cal);
	}
	
	public boolean checkPassword(String username, String password, String key){
		if(admins.containsKey(username) && admins.get(username)[0].equals(password)){
			admins.get(username)[1] = key;
			return true;
		}
		else{
			return false;
		}
	}
	
	public String[] getAdmin(String username){
		return admins.get(username);
	}
	
	public HashMap<String,String[]> getAdmins(){
		return admins;
	}
	
	public HashMap<String, GregorianCalendar> getBanList(){
		return banList;
	}
	
	public User getUser(String username){
		return users.get(username);
	}
	
	public long getUserId(){
		return userId;
	}
	
	public HashMap<String, User> getUsers(){
		return users;
	}
	
	public boolean isAdmin(String username){
		if(admins.containsKey(username))
			return true;
		else
			return false;
	}
	
	public boolean isBan(String key){
		if(banList.containsKey(key)){
			if(banList.get(key).before(new GregorianCalendar())){
				banList.remove(key);
				return false;
			}
			return true;
		}
		return false;
	}
	
	public boolean isUserLinkedToChannel(String username, long channelId){
		if(((Channel)ctrl.getChannel(channelId)).getUsers().contains(username) && getUser(username).getChannels().contains(channelId))
			return true;
		else
			return false;
	}
	
	public void linkUserToChannel(String username, long channelId){
		if(!users.containsKey(username))
			System.out.println("User Service Error - User : " + username + " doesn't exist. Can't join channel : " +channelId);
		else if(ctrl.getChannel(channelId) == null)
			System.out.println("User Service Error - Channel : " + channelId + " doesn't exist. User : "+ username + "couldn'tt join this channel.");
		else if(ctrl.getChannel(channelId) instanceof Channel && !isUserLinkedToChannel(username,channelId)){
			getUser(username).addChannel(channelId);
			((Channel)ctrl.getChannel(channelId)).addUser(username);
			ctrl.sendJoinned(username,channelId);
			System.out.println("User Service - User : " + username + " joinned channel : " + channelId + ".");
		}	
	}
	
	public void listUsers(long chanId){
		ChannelTree ct = ctrl.getChannel(chanId);
		if(ct instanceof ChannelGroup){
			Vector<String> users = new Vector<String>();
			ChannelGroup cg = (ChannelGroup)ct;
			for(long id : cg.getChildren()){
				vectorUsers(id,users);
			}
			Collections.sort(users);
			if(!getUsers().isEmpty()){
				for(String username : users){
						System.out.println(getUser(username).getId() + " U " + username);
				}
			}
		}
		else{
			Channel ch = (Channel)ct;
			Vector<String> users = ch.getUsers();
			Vector<String> moderators = ch.getModerators();
			for(String username : moderators)
				if(users.contains(username))
					users.removeElement(username);
			if(!users.isEmpty()){
				System.out.println("users :\n");
				Collections.sort(users);
				for(String username : users)
					System.out.println(getUser(username).getId() + " U " + username);
			}
			if(!moderators.isEmpty()){
				System.out.println("moderators :\n");
				Collections.sort(moderators);
				for(String username : moderators)
					System.out.println(getUser(username).getId() + " U " + username);
			}
		}
	}
	
	public void logoutUser(String username){
		if(users.containsKey(username)){
			Vector<Long> channels = new Vector<Long>(getUser(username).getChannels());
			for(long chanId : channels){
				unlinkUserToChannel(username,chanId, false);
			}
			users.remove(username);
			if(admins.containsKey(username))
				admins.get(username)[1] = "";
			System.out.println("User Service - User : " + username + " logged out.");
		}
		else
			System.out.println("User Service Error - User : " + username + " doesn't exist. He tried to logout though.");
	}
	
	public void nextUserId(){
		userId++;
	}
	
	public void removeAdmin(String username){
		if(admins.containsKey(username)){
			admins.remove(username);
			System.out.println("User Service - Admin : "+ username+ " was removed.");
		}
		else
			System.out.println("User Service Error - Admin : "+ username+ " doesn't exist. Couldn't remove it.");
	}
	
	public void removeUser(long id){
		boolean removed = false;
		for(String key : users.keySet()){
			if(users.get(key).getId() == id){
				users.remove(key);
				removed = true;
				ctrl.sendWelcome(key);
				System.out.println("User Service - User : "+ key + " was removed.");
			}
		}
		if(!removed)
			System.out.println("User Service Error - Userid : "+ id + " doesn't exist. Couldn't remove it.");
	}
	
	public void removeUser(String username, boolean disconnecting){
		if(users.containsKey(username)){
			if(!disconnecting)
				ctrl.sendWelcome(users.get(username).getKey());
			Vector<Long> chans = new Vector<Long>(getUser(username).getChannels());
			for(long id : chans)
				unlinkUserToChannel(username, id, true);
			users.remove(username);
			System.out.println("User Service - User : "+ username+ " was removed.");
		}
		else
			System.out.println("User Service Error - User : "+ username+ " doesn't exist. Couldn't remove it.");
	}
	
	public void unbanUser(String username){
		unbanUserByKey(getUser(username).getKey());
	}
	
	public void unbanUserByKey(String key){
		banList.remove(key);
	}
	
	public boolean unlinkUserToChannel(String username, long channelId){
		if(isUserLinkedToChannel(username,channelId)){
			getUser(username).removeChannel(channelId);
			((Channel)ctrl.getChannel(channelId)).removeUser(username);
			ctrl.sendLeft(username, channelId);
			System.out.println("User Service - User : " + username + " left channel : " + channelId);
			return true;
		}
		else{
			System.out.println("User Service Error - User : " + username + " wasn't in channel : " + channelId);
			return false;
		}
	}
	
	public void unlinkUserToChannel(String username, long channelId, boolean notify){
		if(unlinkUserToChannel(username, channelId))
			if(notify)
				ctrl.sendLeft(username, channelId);
	}
	
	public boolean usedUsername(String username){
		if(users.containsKey(username)){
			return true;
		}
		else{
			return false;
		}
	}
	
	private void vectorUsers(long chanId, Vector<String> users){
		ChannelTree ct = ctrl.getChannel(chanId);
		if(ct instanceof ChannelGroup){
			ChannelGroup cg = (ChannelGroup)ct;
			for(long id : cg.getChildren()){
				vectorUsers(id,users);
			}
		}
		else{
			Channel ch = (Channel)ct;
			Vector<String>chUsers = ch.getUsers();
			for(String username : chUsers){
				if(!users.contains(username)){
					users.add(username);
				}
			}
		}
	}
	
	public void enable(){
		
	}
	
	public void disable(){
		
	}
	
	public void shutdown(){
		Set<String> usernames = users.keySet();
		for(String s : usernames){
			ctrl.logoutUser(s);
		}
	}
	
	public Vector<String> getKeys(){
		Vector<String> keys = new Vector<String>();
		for(String username : ctrl.getUsers().keySet()){
			keys.add(ctrl.getUser(username).getKey());
		}
		return keys;
	}
}
