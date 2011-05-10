package de.uni_koblenz.jgralab.impl.disk;


public abstract class StorageContainer {

	//TODO implement flag that records changes, so a write needs to be done only if the container content has changed
	
	int id;
	
	/* stores the type of the element or null if the element is null */
	int[] types;
	
	StorageContainer nextInQueue = null;
	
	StorageContainer previousInQueue = null;
	
	//TODO: Remove this field as soon as graph is storage
	public BackgroundStorage backgroundStorage;

	public StorageContainer(int id, int size, BackgroundStorage backgroundStorage) {
		this.id = id;
		types = new int[size];
		this.backgroundStorage = backgroundStorage;
	}

	public StorageContainer() {
		
	}


}
