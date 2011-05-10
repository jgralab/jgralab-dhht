package de.uni_koblenz.jgralab.impl.disk;

import de.uni_koblenz.jgralab.Edge;

public class EdgeContainer extends GraphElementContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2492472176568366922L;
	
	transient Edge[] edges;

	public EdgeContainer(int id, int size, BackgroundStorage backgroundStorage) {
		super(id, size, backgroundStorage);
		edges = new Edge[size];
	}
	

	public EdgeContainer(int id, BackgroundStorage backgroundStorage) {
		super();
		this.id = id;
		this.backgroundStorage = backgroundStorage;
	}
	
	


	
}
