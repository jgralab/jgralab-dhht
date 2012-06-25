package de.uni_koblenz.jgralab.impl.diskv2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;

/**
 * Wrapper class to provide access to a file.
 * 
 * A FileAccess for a given file can be obtained by calling the factory method
 * provided by this class. The object returned by this method then provides methods
 * to write the contents of a ByteBuffer into this file, or to read a number of 
 * bytes from this file, which are then returned in a byte buffer.
 * 
 * Internally, it uses a MappedByteBuffer for increased efficiency.
 *  
 * @author aheld
 *
 */
public class FileAccess {
	
	/**
	 * A map in which accesses to files are stored.
	 */
	public static HashMap<String, FileAccess> files = new HashMap<String, FileAccess>();
	
	/**
	 * The size of the area of the file that is mapped into memory, in bytes.
	 */
	public static final int FILE_AREA = 1024;
	
	/**
	 * The FileChannel used to access the file.
	 */
	private FileChannel channel;

	/**
	 * The part of the file that is mapped into memory.
	 */
	private MappedByteBuffer accessWindow;
	
	/**
	 * Denotes the first byte of the access window.
	 */
	private long firstByte;
	
	/**
	 * Denotes the last byte of the access window.
	 */
	private long lastByte;
	
	// --------------------------[(a)-----------------(b)]------------
	// 
	// The hyphens represent the entire file. The area between [ and ] is 
	// the part of the file mapped into memory. Then, (a) is the first byte, 
	// whereas (b) is the last byte of the area mapped into memory.
	
	/**
	 * Creates a FileAccess to a specific file.
	 * 
	 * @param filename
	 *        The name of the file the constructed object provides access to.
	 */
	private FileAccess(String filename){
		File file = new File(filename + ".dst");
		file.deleteOnExit(); //FIXME: Does not work
		try {
			RandomAccessFile ramFile = new RandomAccessFile(file, "rw");
			FileChannel fileChannel = ramFile.getChannel();
			this.channel = fileChannel;
			//use invalid values to force a remapping at the first read or write operation
			firstByte = lastByte = -1;
			files.put(filename, this);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Error: Could not create file " + filename + ".dst");
		} catch (IOException e) {
			throw new RuntimeException("Error: Could not map file " + filename + ".dst");
		}
	}
	
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
		
		return new FileAccess(filename);
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
	public void write(ByteBuffer content, long index){
		checkAccessWindow(content.capacity(), index);
	
		content.position(0);
		accessWindow.position((int) (index - firstByte));
		
		accessWindow.put(content);
	}
	
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
	public ByteBuffer read(int numBytes, long index){
		checkAccessWindow(numBytes, index);
		
		byte[] readBytes = new byte[numBytes];
		
		accessWindow.position((int) (index - firstByte));
		accessWindow.get(readBytes);
		
		return ByteBuffer.wrap(readBytes);
	}
	
	/**
	 * Checks if the current read or write operation fits into the access window,
	 * i.e. if the part of the file that the operation wants to access is currently
	 * in the memory.
	 * 
	 * If it is, this method does nothing.
	 * If it isn't, the changes made to the part of the file that was mapped to the
	 * memory when this method was called are forced out to the disk. After that, a new
	 * part of the file is mapped into memory. The area that is mapped into memory is 
	 * chosen in such a way that the start position of the read or write operation (i.e.
	 * the byte at which the read or write operation is started) is in the middle
	 * of the area that is mapped to the memory during this method call. 
	 * An exception to this rule occurs if the condition "index < FILE_AREA/2" is met.
	 * In this case, the first FILE_ARE bytes of the file are mapped to the memory.
	 * 
	 * @param bufSize
	 *        The amount of bytes that are read or written in the next operation
	 * @param index
	 *        The starting position of the next read or write operation
	 */
	private void checkAccessWindow(int bufSize, long index){
		if (index < firstByte | index + bufSize > lastByte){
			
			if (index < 512){
				firstByte = 0;
				lastByte = FILE_AREA;
			}
			else {
				firstByte = index - 512;
				lastByte = index + 512;
			}
			
			try {
				if (accessWindow != null) accessWindow.force();
				accessWindow = channel.map(MapMode.READ_WRITE, firstByte, FILE_AREA);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	public String toString(){
		return "Mapping from " + Long.toString(firstByte) + " to " + Long.toString(lastByte);
	}
}
