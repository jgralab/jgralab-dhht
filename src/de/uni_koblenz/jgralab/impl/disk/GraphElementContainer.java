package de.uni_koblenz.jgralab.impl.disk;


public abstract class GraphElementContainer extends StorageContainer {

	public GraphElementContainer(int id, int size, DiskStorageManager backgroundStorage) {
		super(id, size, backgroundStorage);
		incidenceListVersion = new long[size];
		kappa = new long[size];
		sigmaId = new long[size];
		nextElementInGraphId = new long[size];
		previousElementInGraphId = new long[size];
		firstIncidenceId = new long[size];
		lastIncidenceId = new long[size];
	}
	
	public GraphElementContainer() {
		
	}
	
	long[] incidenceListVersion;
	
	long[] kappa;
	
	long[] sigmaId;
	
	long[] nextElementInGraphId;
	
	long[] previousElementInGraphId;
	
	long[] firstIncidenceId;
	
	long[] lastIncidenceId;
	
	AttributeContainer[] attributes;
	

	
}
