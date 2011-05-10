package de.uni_koblenz.jgralab.impl.disk;


public abstract class GraphElementContainer extends StorageContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7263708635173794673L;

	public GraphElementContainer(int id, int size, BackgroundStorage backgroundStorage) {
		super(id, size, backgroundStorage);
		incidenceListVersion = new long[size];
		kappa = new int[size];
		sigmaId = new int[size];
		nextElementInGraphId = new int[size];
		previousElementInGraphId = new int[size];
		firstIncidenceId = new int[size];
		lastIncidenceId = new int[size];
	}
	
	public GraphElementContainer() {
		
	}
	
	long[] incidenceListVersion;
	
	int[] kappa;
	
	int[] sigmaId;
	
	int[] nextElementInGraphId;
	
	int[] previousElementInGraphId;
	
	int[] firstIncidenceId;
	
	int[] lastIncidenceId;
	
	AttributeContainer[] attributes;
	

	
}
