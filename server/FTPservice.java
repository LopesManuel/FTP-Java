
import java.io.IOException;
import java.rmi.RemoteException;



public interface FTPservice extends java.rmi.Remote {

    public String ls(String serverPath) 
            throws java.rmi.RemoteException; 
     
    public String cd(String dir, String serverPath) 
            throws java.rmi.RemoteException; 
    
    public String open() 
            throws java.rmi.RemoteException;

	public boolean put(byte[] buffer, String path) throws IOException;

	byte[] get(String fileName, int offset, int fileSize)
			throws RemoteException;
	public int getFileSize(String string) throws RemoteException;
}
