package de.uni_koblenz.jgralab.impl.disk;

import de.uni_koblenz.jgralab.Vertex;

public class VertexContainer extends GraphElementContainer {

	
	transient Vertex[] vertices;

	public VertexContainer(int id, int size, DiskStorageManager backgroundStorage) {
		super(id, size, backgroundStorage);
		vertices = new Vertex[size];
	}
	
	public VertexContainer(int id, DiskStorageManager backgroundStorage) {
		super();
		this.id = id;
		this.backgroundStorage = backgroundStorage;
	}

		
	
}
