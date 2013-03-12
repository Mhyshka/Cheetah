package run;

import java.util.Scanner;

import data.Channel;

public class Console extends Thread{
	private Scanner sc;
	private Controller ctrl;
	private static Console console;
	
	
	public static Console getInstance(Controller newCtrl){
		if(console != null){
			return console;
		}
		else{
			console = new Console(newCtrl);
			return console;
		}
	}
	
	public Console(Controller newCtrl){
		super();
		ctrl = newCtrl;
	}
	
	@Override
	public void run() {
		sc = new Scanner(System.in);
		
		while(ctrl.isRunning()){
			sc.reset();
			if(sc.hasNext()){
				String s[] = sc.nextLine().split("\\s+");
				
				switch( s[0]){
					case "list" : listCommand(s);
					break;
					
					case "shutdown" : shutdownCommand(s);
					break;
					
					case "kick" : kickCommand(s);
					break;
					
					case "ban" : banCommand(s);
					break;
					
					case "delete" : deleteCommand(s);
					break;
					
					case "new" : newCommand(s);
					break;
					
					case "move" : moveCommand(s);
					break;
					
					default : System.out.println("Unknown command, 'list commands' to display all usable commands.");
				}
			}
		}
	}
	
	private void banCommand(String args[]){
		// TODO 
		// Non fonctionnelle.
		switch(args[1]){
			case "?": System.out.println("kick : Kick the user given in parameter." +
					"\neg : 'kick AliGuetto38' will kick the user with the name AliGuetto38" +
					"\nUsable with : username <name> .. <reason> ; userid <id> .. <reason>, ip <ip> .. <reason>");
			break;
			
			case "user" : //ctrl.ban();
			break;
			
			case "ip" : //ctrl.ban();
			break;
			
			default : System.out.println("Illegal argument 1. Use 'commandName ?' to obtain further informations.");
		}
	}
	
	
	
	private void deleteCommand(String args[]){
		if(args.length == 2 || args.length == 3 || args.length == 4){
			switch(args[1]){
				case "?": System.out.println("delete : Delete the variable given in parameter. Might delete others variables linked with the one given. " +
						"\neg : 'delete userid 12' will wipe everything about this user out. Channels, image, files shares and will even log him out." +
						"\nUsable with : userid <id> ; username <name> ; admin <name> ;channelgroup <id>.. <*>(Recursive) ; channel <id> ; file <id>");
				break;
				
				case "admin" : ctrl.removeAdmin(args[2]);
				break;
				
				case "username" : ctrl.removeUser(args[2],false);
				break;
				
				case "userid" : 
					try{
						long id = Long.parseLong(args[2]);
						ctrl.removeUser(id);
					}
					catch(NumberFormatException e){
						System.out.println("Argument 2 NaN.");
					}
					
				break;
				
				case "channelgroup" : 
					try{
						long id = Long.parseLong(args[2]);
						if(args.length == 4)
							if(args[3].equals("*"))
								ctrl.removeChannelGroup(id, false);
							else
								System.out.println("Argument 3 must be * if used. (Will remove every channels and channelGroups include)");
						else
							ctrl.removeChannelGroup(id, true);
					}
					catch(NumberFormatException e){
						System.out.println("Argument 2 NaN.");
					}
				break;
				
				case "channel" :				
					try{
						long id = Long.parseLong(args[2]);
						ctrl.removeChannel(id);
					}
					catch(NumberFormatException e){
						System.out.println("Illegal argument 2 : NaN. Use 'commandName ?' to obtain further informations.");
					}
				break;
				
				case "file" : // TODO
				break;
				
				default : System.out.println("Illegal argument 1. Use 'commandName ?' to obtain further informations.");
			}
		}
		else
			System.out.println("Illegal number of parameters. Use 'commandName ?' to obtain further informations.");
	}
	
	private void kickCommand(String args[]){
		// TODO 
		// Non fonctionnelle.
		switch(args[1]){
			case "?": System.out.println("kick : Kick the user given in parameter." +
					"\neg : 'kick username AliGuetto38' will kick the user with the name AliGuetto38" +
					"\nUsable with : username <name> .. <reason> ; userid <id> .. <reason>, ip <ip> .. <reason>");
			break;
			
			case "user" : //ctrl.kick();
			break;
			
			case "ip" :// ctrl.kick();
			break;
			
			default : System.out.println("Illegal argument 1. Use 'commandName ?' to obtain further informations.");
		}
	}
	
	private void listCommand(String args[]){
		if(args.length == 2 || args.length == 3){
			switch(args[1]){
				case "?": System.out.println("list : Display every items of an array. " +
						"\neg : 'list commands' will display all the different commands known by the console." +
						"\nUsable with : users .. <chanId>, commands, channels .. <id>, whispers");
				break;
				
				case "users" : 
					if(args.length == 3){
						try{
							long id = Long.parseLong(args[2]);
							ctrl.listUsers(id);
						}
						catch(NumberFormatException e){
							System.out.println("Illegal argument 2 : NaN. Use 'commandName ?' to obtain further informations.");
						}
					}
					else{
						ctrl.listUsers(0);
					}
				break;
				
				case "commands" : ctrl.listCommands();
				break;
				
				case "channels" : 
					if(args.length ==3)
						try{
							long id = Long.parseLong(args[2]);
							ctrl.listChannels(id);
						}
					catch(NumberFormatException e){
						System.out.println("Illegal argument 2 : NaN. Use 'commandName ?' to obtain further informations.");
					}
					else
						ctrl.listChannels(0);

				break;
				
				case "whispers" : ctrl.listWhispers();
				break;
				
				case "ban" : // TODO affiche la liste des IP / username ban avec la date.
				break;
				
				default : System.out.println("Illegal argument 1. Use 'commandName ?' to obtain further informations.");
			}
		}
		else System.out.println("Illegal number of parameters. Use 'commandName ?' to obtain further informations.");
	}
	
	private void moveCommand(String args[]){
		// TODO 
		// Non fonctionnelle.
	}
	
	private void newCommand(String args[]){
		if(args.length < 2){
			System.out.println("Illegal number of parameters. Use 'commandName ?' to obtain further informations.");
		}
		else{
			switch(args[1]){
				case "?": System.out.println("new : Create a new variable." +
						"\neg : 'new channel chan14' will create a new Channel name Chan14 in the root ChannelGroup." +
						"\nUsable with : admin <name> <password> ; channel <name> .. <parentId> .. <password>  ; channelgroup <name> .. <parentId>");
				break;
				
				case "admin" : 
					if(args.length == 4){
						ctrl.newAdmin(args[2], args[3]);
					}
					else
						System.out.println("Illegal number of parameters. Use 'commandName ?' to obtain further informations.");
				break;
				
				case "channel" : 
					newChannel(args);
				break;
				
				case "channelgroup" :
					newChannelGroup(args);
				break;
				
				default : System.out.println("Illegal argument 1. Use 'commandName ?' to obtain further informations.");
			}
		}
	}
	
	private void newChannel(String args[]){
		if(args.length >= 3){
			String name = "";
			int i = 2;
			if(args[i].startsWith("\"")){
				while(!args[i].endsWith("\"") && i < args.length-1){
					if(!name.isEmpty())
						name += " ";
					name += args[i];
					i++;
				}
				if(!name.isEmpty())
					name += " ";
				name += args[i];
				i++;
				
				name=name.substring(1, name.length()-1);

				if(args[i-1].endsWith("\"")){
					if(args.length == i){
						ctrl.newChannel(name, 0, false);
					}
					else if(args.length == i+1)
							try{
								long parentId = Long.parseLong(args[i]);
								ctrl.newChannel(name, parentId, false);
							}
							catch(NumberFormatException e){
								 System.out.println("Argument "+i+" NaN.");
							}
					else if(args.length == i+2){
							try{
								long parentId = Long.parseLong(args[i+1]);
								long id = ctrl.newChannel(name, parentId, false);
								((Channel)ctrl.getChannel(id)).setPassword(args[i+1]);
							}
							catch(NumberFormatException e){
								 System.out.println("Argument "+i+" NaN.");
							}
					}
				}
				else
					System.out.println("Invalid name for channel. Has to be between quotes \"\".");
			}
			else
				System.out.println("Invalid name for channel. Has to be between quotes \"\".");
		}
		else
			System.out.println("Illegal number of parameters. Use 'commandName ?' to obtain further informations.");
	}
	
	private void newChannelGroup(String args[]){
		int i = 2;
		String name = "";
		if(args[i].startsWith("\"")){
			while(!args[i].endsWith("\"") && i < args.length-1){
				if(!name.isEmpty())
					name += " ";
				name += args[i];
				i++;
			}
			if(!name.isEmpty())
				name += " ";
			name += args[i];
			i++;
			
			name=name.substring(1, name.length()-1);
			
			
			if(args[i-1].endsWith("\"")){
				if(args.length == i){
					ctrl.newChannelGroup(name, 0);
				}
				else if(args.length == i+1)
					try{
						long parentId = Long.parseLong(args[i]);
						ctrl.newChannelGroup(name, parentId);
					}
					catch(NumberFormatException e){
						 System.out.println("Argument "+i+" NaN.");
					}
			}
			else
				System.out.println("Invalid name for channelgroup. Has to be between quotes \"\".");
		}
		else
			System.out.println("Invalid name for channelgroup. Has to be between quotes \"\".");
	}

	private void shutdownCommand(String args[]){
		if(args.length >= 2){
			String reason = "";
			if(args[1].startsWith("\"") && args[args.length-1].endsWith("\"")){
				for(int i=1;i<args.length;i++){
					if(!reason.isEmpty())
						reason += " ";
					reason += args[i];
				}
				reason=reason.substring(1, reason.length()-1);
			}
			ctrl.shutdown(reason);
		}
		else if(args.length == 1){
			ctrl.shutdown("");
		}
		else{
			System.out.println("Illegal number of parameters.");
		}
	}
	
	public void listCommands(){
		System.out.println("ban - To ban users\n" +
				"delete - To delete variables\n" +
				"edit - To modify some values of a variable\n" +
				"list - To list variables\n" +
				"move - To move a Channel\n" +
				"new - To create a new variable\n" +
				"search - To look for something\n" +
				"shutdown - To stop the server\n");
	}
}
