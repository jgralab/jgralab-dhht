package de.uni_koblenz.jgralab.impl.disk;


public abstract class StorageContainer {

	//TODO implement flag that records changes, so a write needs to be done only if the container content has changed
	
	int id;
	
	/* stores the type of the element or null if the element is null */
	long[] types;
	
	StorageContainer nextInQueue = null;
	
	StorageContainer previousInQueue = null;

	public DiskStorageManager backgroundStorage;

	public StorageContainer(int id, int size, DiskStorageManager backgroundStorage) {
		this.id = id;
		types = new long[size];
		this.backgroundStorage = backgroundStorage;
	}

	public StorageContainer() {
		
	}


}
