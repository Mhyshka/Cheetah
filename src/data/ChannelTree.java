package data;

public abstract class ChannelTree {
	private String name;
	private long id;
	private ChannelGroup parent;
	
	public long getId(){
		return this.id;
	}
	public String getName() {
		return this.name;
	}

	public ChannelGroup getParent() {
		return this.parent;
	}
	public void setId(long id){
		this.id = id;
	}

	public void setName(String newName){
		this.name = newName;
	}
	public void setParent(ChannelGroup chGroup){
		if(parent != null)
			parent.removeChild(id);
		this.parent = chGroup;
		if(chGroup != null)
			chGroup.addChild(id);
	}
}
