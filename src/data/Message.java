package data;


public class Message {
	private long id;
	private String op;
	private long chanId;
	private int color;
	private boolean italic, bold;
	private String text;
	private String date;
	
	public Message(long newId, String opName, long newChanId, String newText){
		id = newId;
		op = opName;
		chanId = newChanId;
		text = newText;
	}
	
	public long getChannel() {
		return chanId;
	}
	
	public int getColor() {
		return color;
	}
	
	public String getDate() {
		return date;
	}
	
	public long getId() {
		return id;
	}
	
	public String getOpId() {
		return op;
	}
	public String getText() {
		return text;
	}
	
	public boolean isBold() {
		return bold;
	}
	public boolean isItalic() {
		return italic;
	}
	
	public void setBold(boolean bold) {
		this.bold = bold;
	}
	
	public void setChannel(long newChanId) {
		this.chanId = newChanId;
	}
	public void setColor(int color) {
		this.color = color;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public void setItalic(boolean italic) {
		this.italic = italic;
	}

	public void setOp(String newOpName) {
		this.op = newOpName;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
