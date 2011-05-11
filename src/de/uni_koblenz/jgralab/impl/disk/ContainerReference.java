package de.uni_koblenz.jgralab.impl.disk;

import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;

public abstract class ContainerReference<T extends StorageContainer> extends WeakReference<T> {
	
	
	DiskStorageManager backgroundStorage;
	
	private boolean reused = false;
	
	int id;
	
	int[] types = null;
	
	boolean changed = false;
	
	ContainerReference<?> nextInReuseQueue = null;
	
	public synchronized void setReused() {
		reused = true;
	}
	
	public synchronized boolean isReused() {
		boolean returnVal = reused;
		reused = false;
		return returnVal;
	}
	
	
	public ContainerReference(T container, ReferenceQueue<? super StorageContainer> queue) {
		super(container, queue);
	}
	
	abstract void write(FileChannel channel) throws IOException ;

}
