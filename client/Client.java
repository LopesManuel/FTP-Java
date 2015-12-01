


import java.io.*;
import java.net.*;
import java.rmi.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Client {
	static FTPprotocol protocol = null;
	static FTPservice ftpSrv;
	
	public static void main(String[] args) throws IOException {
	       	
			protocol = new FTPprotocol();
			protocol.localPath = new java.io.File( "." ).getCanonicalPath();
			Scanner stdin = new Scanner(System.in);
			String cmd = null;
			String output = null;
			String serverOutput = null;
                           
			try { 
	        	System.out.print(protocol.localPath +"> ");
				while(stdin.hasNextLine()){
					serverOutput = null;
					cmd = stdin.nextLine();
					output = protocol.processInput(cmd, ftpSrv);
					System.out.print(output);
					if(protocol.state == FTPprotocol.ESTABLISHING_CONNECTION){
						ftpSrv = (FTPservice)Naming.lookup("rmi://"+ protocol.url +"/FTPservice");
						 protocol.serverPath = ftpSrv.open();
						 if(!protocol.serverPath.equals("0")){
							serverOutput = "You are now connected to the FTP server\n";
							protocol.state = FTPprotocol.CONNECTION_ESTABLISHED;
						 }else{
							serverOutput = "You were unhable to connect to the FTP server\n";
						 }
					}
					if(serverOutput != null)
						System.out.print(serverOutput);
		        	System.out.print(protocol.localPath +" | " + protocol.serverPath + " > ");

				}
	        } 
	        catch (MalformedURLException murle) { 
	            System.out.println(); 
	            System.out.println("MalformedURLException"); 
	            System.out.println(murle); 
	        } 
	        catch (RemoteException re) { 
	            System.out.println(); 
	            System.out.println("RemoteException"); 
	            System.out.println(re); 
	        } 
	        catch (NotBoundException nbe) { 
	            System.out.println(); 
	            System.out.println("NotBoundException"); 
	            System.out.println(nbe); 
	        } 
	        catch (
	            java.lang.ArithmeticException ae) { 
	            System.out.println(); 
	            System.out.println( "java.lang.ArithmeticException"); 
	            System.out.println(ae); 
	        } 
	}
}
