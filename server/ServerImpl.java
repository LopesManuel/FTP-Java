

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;


public class ServerImpl  extends java.rmi.server.UnicastRemoteObject implements FTPservice{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected ServerImpl() throws RemoteException {
		super();
	}

	@Override
	public byte[] get(String fileName, int offset, int chunkSize) throws RemoteException {

		try
		{
			File file = new File(fileName);
			byte buffer[]=new byte[(int)file.length()];
			BufferedInputStream input=new BufferedInputStream(new FileInputStream(fileName));
			int n = input.read(buffer,offset,chunkSize);
			input.close();
			if (n == -1) return null;
			return(buffer);
		}
		catch(Exception e)
		{
			System.out.println("FileImpl : "+e.getMessage());
			e.printStackTrace();
			return(null);
		}
	}	

	@Override
	public String  ls(String serverPath) throws RemoteException {

		// Directory path here
		String output = "";
		File folder = new File(serverPath);
		File[] listOfFiles = folder.listFiles(); 
			 
		 for (int i = 0; i < listOfFiles.length; i++) {
		     output = output +listOfFiles[i].getName() + "\n";
		 }
		 return output;
	}

	@Override
	public String cd(String dir, String serverPath) throws RemoteException {

		if(dir.equals("..")){
		      String [] fields = serverPath.split("/");
		      serverPath = "";
		      for(int i =0; i < fields.length - 1; i++){
		    	  if(!fields[i].equals(""))
		    		  serverPath = serverPath +"/" + fields[i];
		      }
		}
		else{
			File folder = new File(serverPath);
			File[] listOfFiles = folder.listFiles(); 
				 
			for (int i = 0; i < listOfFiles.length; i++) {
			     if(listOfFiles[i].isDirectory()){			    	 
					 if(dir.equals(listOfFiles[i].getName())){
						 serverPath = serverPath + "/" + dir;
				    	return serverPath;
				     }
				 }
			 }
			 return "NA";
		}
		return serverPath;
	}

	@Override
	public String open() throws RemoteException {

		try {
			System.out.println("Connection established from ip:"+ RemoteServer.getClientHost());
		} catch (ServerNotActiveException e) {

			e.printStackTrace();
		}
		String path = "0";
		try {
			path =	new java.io.File( "." ).getCanonicalPath();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return path;
	}

	@Override
	public boolean put(byte[] filedata, String path) throws IOException {
		
	    File file=new File(path);
	    
	    //convert array of bytes into file
	    FileOutputStream fileOutput=new FileOutputStream(path, true);
	    fileOutput.write(filedata);
	    fileOutput.flush();
	    fileOutput.close();
	    return true;
	}
	@Override

	public int getFileSize(String path)throws RemoteException{
		File file = new File(path);
		return (int) file.length();
	}
}
