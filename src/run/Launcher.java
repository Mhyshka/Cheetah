package run;


public class Launcher {
	private static int port;
	
	public static void main(String args[]){
		if(args.length == 1){
			try{
				port = Integer.parseInt(args[0]);
			}
			catch(NumberFormatException e){
				System.out.println("Port NumberFormatException, using 34253.");
				port = 34253;
			}
		}
		else{
			System.out.println("Port Missing, using 34253.");
			port = 34253;
		}
		Controller.start(port);
	}
}