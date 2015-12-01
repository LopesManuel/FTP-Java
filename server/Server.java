import java.io.IOException;
import java.rmi.*;
import java.net.*;
import java.util.*;

public class Server {
	public Server() throws IOException{
           String codebase = new java.io.File( "." ).getCanonicalPath();
           System.setProperty( "java.rmi.server.codebase", codebase );
           System.setProperty( "java.security.policy", codebase+"/server.policy" );
	   System.setSecurityManager( new RMISecurityManager() );
	      try {
	    	  FTPservice s = new ServerImpl();
	    	  String hostname = InetAddress.getLocalHost().getHostAddress();
                  System.out.println("this host IP is " + hostname);
	    	  Naming.rebind("rmi://"+hostname+":4099/FTPservice", s);
	    	  
	        } catch (Exception e) {
	          System.out.println("Trouble: " + e);
	        }
	}
	public static void main (String[] args) throws IOException{  
		new Server();
	}
}
