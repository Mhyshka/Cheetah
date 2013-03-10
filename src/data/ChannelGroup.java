package data;

import java.util.Vector;

public class ChannelGroup extends ChannelTree{
	private Vector<Long> children;
	

	public ChannelGroup(long id, String name, ChannelGroup parent){
		setName(name);
		setId(id);
		setParent(parent);
		children = new Vector<Long>();
	}
	
	public void addChild(long newChannelTreeId){
		children.add(newChannelTreeId);
	}
	public Vector<Long> getChildren() {
		return children;
	}
	public void removeChild(long rmChannelTreeId){
		children.remove(rmChannelTreeId);
	}
	public void setChildren(Vector<Long> children) {
		this.children = children;
	}
}
