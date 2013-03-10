package run;

import java.util.HashMap;

import data.Channel;
import data.ChannelGroup;
import data.ChannelTree;


public class ChannelManager {
	private static ChannelManager manager;
	private HashMap<Long,ChannelTree> channelTree;
	private Controller ctrl;
	private long channelId;
	
	
	public static ChannelManager getInstance(Controller ctrl){
		if(manager != null)
			return manager;
		else{
			manager = new ChannelManager(ctrl);
			return manager;
		}
	}
	
	private ChannelManager(Controller ctrl){
		this.ctrl = ctrl;
		channelId = 0;
		channelTree = new HashMap<Long,ChannelTree>();
		System.out.println("Channel Service - initialized.");
		if(!this.ctrl.isEnabled(Controller.SERVICE.CHANNEL_SERVICE))
			System.out.println("Channel service - is stopped.");
		else
			System.out.println("Channel service - is running.");
		if(getChannelId() == 0){
			addChannelGroup(new ChannelGroup(0, "root", null));
			nextChannelId();
			newChannel("General", (long)0, false);
		}
	}
	
	public void addChannel(Channel newChannel){
		if(!this.ctrl.isEnabled(Controller.SERVICE.CHANNEL_SERVICE))
			System.out.println("Channel service - is stopped. Can't add a new Channel.");
		else{
			channelTree.put(newChannel.getId(),newChannel);
		}
	}
	
	public void addChannelGroup(ChannelGroup newChannelGroup){
		if(!this.ctrl.isEnabled(Controller.SERVICE.CHANNEL_SERVICE))
			System.out.println("Channel service - is stopped. Can't add a new ChannelGroup.");
		else{
			channelTree.put(newChannelGroup.getId(),newChannelGroup);
		}
			
	}

	public ChannelTree getChannel(long chanId){
		return channelTree.get(chanId);
	}
	
	public long getChannelId(){
		return channelId;
	}
	
	public HashMap<Long,ChannelTree> getTree(){
		return channelTree;
	}
	
	public boolean isChannelGroup(long chanId){
		if(getChannel(chanId) instanceof ChannelGroup)
			return true;
		else
			return false;
	}
	
	public long newChannel(String name, long parentId, Boolean isWhisper){
		if(getChannel(parentId)!= null){
			if(isChannelGroup(parentId)){
				long id = getChannelId();
				addChannel(new Channel(id, name, (ChannelGroup)getChannel(parentId), isWhisper));
				System.out.println("Channel Service - Channel created.");
				nextChannelId();
				return id;
			}
			else{
				System.out.println("Channel Service Error - parentId given isn't refereing to a ChannelGroup.");
			}
		}
		else
			System.out.println("Channel Service Error - parentId given does not exist.");
		return 0;
	}
	
	public long newChannelGroup(String name, long parentId){
		if(getChannel(parentId)!= null){
			if(isChannelGroup(parentId)){
				long id = getChannelId();
				addChannelGroup( new ChannelGroup(id, name, (ChannelGroup)getChannel(parentId)));
				System.out.println("Channel Service - ChannelGroup created.");
				nextChannelId();
				return id;
			}
			else{
				System.out.println("Channel Service Error - parentId given isn't refereing to a ChannelGroup.");
			}
		}
		else
			System.out.println("Channel Service Error - parentId given does not exist.");
		return 0;
	}
	
	public long nextChannelId(){
		return channelId++;
	}	
	
	public void removeChannel(long id){
		if(!this.ctrl.isEnabled(Controller.SERVICE.CHANNEL_SERVICE))
			System.out.println("Channel service - is stopped. Can't remove a new Channel Component.");
		else{
			if(getChannel(id) instanceof ChannelGroup){
				System.out.println("Channel service - Wrong Channel ID. This is a ChannelGroup ID.");
			}
			else{
				((Channel)getChannel(id)).getParent().removeChild(id);
				channelTree.remove(id);
				System.out.println("Channel service - Channel : "+ id + " has been removed.");
			}
		}
	}
	
	public void removeChannelGroup(long id, boolean lenient){
		if(!this.ctrl.isEnabled(Controller.SERVICE.CHANNEL_SERVICE))
			System.out.println("Channel service - is stopped. Can't remove a new Channel Component.");
		else{
			if(getChannel(id) instanceof ChannelGroup){
				if(lenient){
					if(getChannel(id).getParent() != null)
						for(long cid : ((ChannelGroup)channelTree.get(id)).getChildren())
								getChannel(cid).setParent((ChannelGroup)getChannel(getChannel(id).getParent().getId()));
					if( id!= 0){
						channelTree.remove(id);
						System.out.println("Channel service - ChannelGroup : "+ id + " has been removed. His children has been given to his parent.");
					}
					else{
						System.out.println("Channel service - ChannelGroup root can't be removed.");
					}
				}
				else{
					removeChildren((ChannelGroup)getChannel(id));
					if( id!= 0){
						channelTree.remove(id);
						System.out.println("Channel service - ChannelGroup : "+ id + " has been removed. His children has been removed.");
					}
					else{
						System.out.println("Channel service - ChannelGroup root can't be removed. His children has been deleted though.");
					}
					
				}
			}
			else
				System.out.println("Channel service - Wrong ChannelGroup ID. This is a Channel ID.");
		}
	}
	
	public HashMap<Long, ChannelTree> getChannels(){
		return channelTree;
	}
	
	public void removeChildren(ChannelGroup channelGroup){
		for(long cid : channelGroup.getChildren()){
			if(getChannel(cid) instanceof ChannelGroup){
				removeChildren((ChannelGroup)getChannel(cid));
			}
			else{
				removeChannel(cid);
			}
		}
	}
	
	public void listChannels(long chanId, String indents){
		String str = "";
		Channel childC;
		
		if(getChannel(chanId) instanceof ChannelGroup){
			ChannelGroup cg = (ChannelGroup)getChannel(chanId);
			str += cg.getId() + " G " + cg.getName();
			System.out.println(indents + str);
			
			indents += "  ";
			for(long id : cg.getChildren()){
				str = "";
				if(getChannel(id) instanceof ChannelGroup){
					listChannels(id, indents);
				}
				else{
					childC = (Channel)getChannel(id);
					str += id + " C " + childC.getName() + " - " + childC.getUsers().size() + " users" + " - owner : "+childC.getOwner();
					System.out.println(indents + str);
				}
			}
		}
		else{
			System.out.println("Channel Service Error - Can't list the children of a Channel that isn't a ChannelGroup.");
		}
	}
	
	public void enable(){
		
	}
	
	public void disable(){
		
	}
}
