package de.uni_koblenz.jgralab.impl.disk;


public abstract class GraphElementContainer extends StorageContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7263708635173794673L;

	public GraphElementContainer(int id, int size, DiskStorageManager backgroundStorage) {
		super(id, size, backgroundStorage);
		incidenceListVersion = new long[size];
		kappa = new int[size];
		sigmaId = new long[size];
		nextElementInGraphId = new long[size];
		previousElementInGraphId = new long[size];
		firstIncidenceId = new long[size];
		lastIncidenceId = new long[size];
	}
	
	public GraphElementContainer() {
		
	}
	
	long[] incidenceListVersion;
	
	int[] kappa;
	
	long[] sigmaId;
	
	long[] nextElementInGraphId;
	
	long[] previousElementInGraphId;
	
	long[] firstIncidenceId;
	
	long[] lastIncidenceId;
	
	AttributeContainer[] attributes;
	

	
}
