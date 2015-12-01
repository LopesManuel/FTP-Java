

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Scanner;


public class FTPprotocol {
    public static final int WAITING = 0;
    public static final int ESTABLISHING_CONNECTION = 1;
    public static final int CONNECTION_ESTABLISHED = 2;
    public static final int SENDINGFILE = 3;
  
    public int state = WAITING;
    public String url = null;
    
	public String localPath = null; 
	public String serverPath = null; 
    
	private String[] commands = { "open", "lcd", "cd", "ls", "put", "get", "exit", "help" };
    private String help =  "1- open <machine> : abre conexão com o servidor myFTP na máquina desejada\n"
    					    + "2- lcd <directory> : muda o directório corrente do cliente para directory (o directório tem de existir)\n"
    						+ "3- cd <directory> : muda o directório corrente do servidor para directory (o directório tem de existir)\n"
    					    + "4- ls : faz listagem do conteudo do directório corrente no servidor\n"
    						+ "5- put <filename> : transfere o ficheiro filename para o directório corrente do servidor mantendo o nome\n"
    					    + "6- get <filename> : transfere o ficheiro filename para o directório corrente do cliente mantendo o nome\n"
    					    + "7- exit : termina a sessão entre o cliente e o servidor\n";
	private Scanner stdin;
    
    
	public String processInput(String input, FTPservice service) throws IOException {
		String cmdTemp = null;
		String output = null;
        String urlT= null;
        
 		if(input == null || input.equals(""))
         	return "";
 		
		stdin = new Scanner(input);
		cmdTemp = stdin.next();
		if(stdin.hasNext())
			urlT = stdin.next();

		boolean isCommand = isCommand(cmdTemp);
        if(!isCommand){
        	output= "Type \"help\" to view the list of commands\n";
        	return output;
        }
        if(input.equals(commands[commands.length-1])){
        	output= help;
        	return output;
        }
        if(cmdTemp.equals(commands[6])){
    		System.exit(0);
    	}
        //lcd
    	if(cmdTemp.equals(commands[1])){
    		if(urlT.equals("..")){
  		      String [] fields = localPath.split("/");
  		    localPath = "";
  		      for(int i =0; i < fields.length - 1; i++){
  		    	  if(!fields[i].equals(""))
  		    		localPath = localPath +"/" + fields[i];
	  		      }
	  		}
	  		else{
	  			File folder = new File(localPath);
	  			File[] listOfFiles = folder.listFiles(); 
	  				 
	  			for (int i = 0; i < listOfFiles.length; i++) {
	  			     if(listOfFiles[i].isDirectory()){			    	 
	  					 if(urlT.equals(listOfFiles[i].getName())){
	  						localPath = localPath + "/" + urlT;
	  				    	return "\n";
	  				     }
	  				 }
	  			 }
	  			 return "No such file or directory\n";
	  		}
    	}
    	//ends lcd
        if (state == WAITING) {
        	if(cmdTemp.equals(commands[0])){
        		output = "Connecting to "+ urlT + "..\n";  	
        		url = urlT;
        		state = ESTABLISHING_CONNECTION;
        	}
        	else{
        		output = "You need to connect to the server first.. see --help\n";  	
        		return output;
        	}
        } 
        else if( state == CONNECTION_ESTABLISHED){
        	// listing in server command
        	if(cmdTemp.equals(commands[3])){
					return service.ls(serverPath);
        	}
        	// muda o directório corrente do servidor para urlT
        	else if(cmdTemp.equals(commands[2])){
    			String tempServerPath = service.cd(urlT, serverPath);
				if(tempServerPath.equals("NA"))
					 return "No such file or directory\n";
				serverPath = tempServerPath;
        	}
        	else if(cmdTemp.equals(commands[5])){
        		output = get(service, urlT);
        	}
        	else if(cmdTemp.equals(commands[4])){
        		output = put(service, urlT);
        	}
        }
        
        return output;
	}
	private boolean isCommand(String cmd){
		if(cmd != null){
			int c = commands.length;
			for(int i=0; i < c; i++ )
				if(cmd.equals(commands[i]))
					return true;
		}
		return false;
	}
	private String get(FTPservice service, String urlT) throws IOException{
		
	 	File file=new File(localPath + "/" + urlT);

	 	if(!file.exists()) {
			file.createNewFile();
		} 
		else{
			System.out.println("There's already a file with that name.\n Do you want to overwrite it? Yes/No ");
		    stdin = new Scanner(System.in);
			String yn = stdin.next();
			boolean answer = false;
			while(!answer){
				if(yn.equals("Yes")){
        			file.createNewFile();
        			answer= true;
				}
				else if(yn.equals("No")){
					return "";
				}
			}
		}
	    //convert array of bytes into file
		int chunkSize = 10*1024*1024;
		int fileSize = service.getFileSize(serverPath + "/"+ urlT);
		int offset = 0;
		while (fileSize > chunkSize) {
			byte[] buffer = service.get(serverPath + "/"+ urlT, offset, chunkSize);
			FileOutputStream fileOutput = new FileOutputStream(localPath, true);
			fileOutput.write(buffer, 0, chunkSize);
			fileOutput.close();
			fileSize -= chunkSize;
		}
		
		if (fileSize != 0) {
			byte[] buffer = service.get(serverPath + "/"+ urlT, offset, fileSize);
			FileOutputStream fileOutput = new FileOutputStream(localPath, true);
			fileOutput.write(buffer, 0, fileSize);
			fileOutput.close();
		}
			
		return "File sucessfully downloaded.\n";
	}
	public String put( FTPservice service, String fileName) throws RemoteException {
		// TODO Auto-generated method stub
		try
		{
			int n = -1;
			byte buffer[] = new byte[10*1024*1024];
			
			BufferedInputStream input=new BufferedInputStream(new FileInputStream(localPath + "/" + fileName));
			while((n = input.read(buffer)) > -1 ){
					service.put(buffer, serverPath + "/" + fileName);
			}
			return("File sucessfully uploaded.\n");
		}
		catch(Exception e)
		{
			System.out.println("FileImpl : "+e.getMessage());
			e.printStackTrace();
			return(null);
		}
	}	
}
