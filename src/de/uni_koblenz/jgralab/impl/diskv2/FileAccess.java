package de.uni_koblenz.jgralab.impl.diskv2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

/**
 * Wrapper class to provide access to a file.
 * 
 * A FileAccess for a given file can be obtained by calling the factory method
 * provided by this class. The object returned by this method then provides methods
 * to write the contents of a ByteBuffer into this file, or to read a number of 
 * bytes from this file, which are then returned in a byte buffer.
 * 
 * Internally, it uses a MappedByteBuffer for increased efficiency if the operating
 * system isn't Windows. If Windows is used, it sticks with a FileChannel because a
 * bug in Java makes it impossible for files to be deleted if it has been 
 * accessed via a MappedByteBuffer at any point.
 *  
 * @author aheld
 *
 */
public abstract class FileAccess {
	
	/**
	 * A map in which accesses to files are stored.
	 */
	private static HashMap<String, FileAccess> files = new HashMap<String, FileAccess>();
	
	/**
	 * Checks if the used OS is windows
	 */
	private static boolean windows = isWindows();
	
	/**
	 * The FileChannel used to access the file.
	 */
	protected FileChannel channel;
	
	/**
	 * Factory method that provides a FileAccess object for a specific file.
	 * If such an object was created previously, that object is returned. 
	 * Else, a new object is created and returned.
	 * 
	 * @param filename
	 *        The name of the file to access. The suffix ".dst" is added internally. 
	 * @return An access to the named file. 
	 */
	public static FileAccess getFileAccess(String filename){
		FileAccess file = files.get(filename);
		
		if (file != null) return file;
		
		FileAccess fileAccess;
		
		if (windows){
			fileAccess = new FileAccessForWindows(filename);
		}
		else {
			fileAccess = new FileAccessDefault(filename);
		}
		
		files.put(filename, fileAccess);
		
		addShutdownHook();
		
		return fileAccess;
	}
	
	/**
	 * Writes the contents of a ByteBuffer into the file that this object provides
	 * access to.
	 * 
	 * @param content
	 *        The ByteBuffer whose content is written to the file
	 * @param index
	 *        The position in the file to which the content is written, in bytes
	 */
	public abstract void write(ByteBuffer content, long index);
	
	/**
	 * Read the content of a file from a given position.
	 * 
	 * @param numBytes
	 *        The amount of bytes to be read
	 * @param index
	 *        The position in the file from which to start reading
	 * @return
	 *        A byte buffer containing the requested bytes
	 */
	public abstract ByteBuffer read(int numBytes, long index);
	
	/**
	 * Method that returns true if the used operating system is windows
	 * 
	 * @return true if the OS is Windows, false otherwise 
	 * 
	 * @author mkyong
	 */
	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("win") >= 0);
	}
	
	private static void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		        for (FileAccess f: files.values()){
		        	try {
						f.channel.close();
					} catch (IOException e) {
						throw new RuntimeException("Unable to close FileChannel");
					}
		        }
		    }
		});
	}
}
