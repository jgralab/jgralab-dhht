package de.uni_koblenz.jgralab.impl.disk;

import de.uni_koblenz.jgralab.Vertex;

public class VertexContainer extends GraphElementContainer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2492472176568366922L;
	
	transient Vertex[] vertices;

	public VertexContainer(int id, int size, BackgroundStorage backgroundStorage) {
		super(id, size, backgroundStorage);
		vertices = new Vertex[size];
	}
	
	public VertexContainer(int id, BackgroundStorage backgroundStorage) {
		super();
		this.id = id;
		this.backgroundStorage = backgroundStorage;
	}

		
	
}
