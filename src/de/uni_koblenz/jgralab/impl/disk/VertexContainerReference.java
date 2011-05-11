package de.uni_koblenz.jgralab.impl.disk;

import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.nio.channels.FileChannel;

import de.uni_koblenz.jgralab.Vertex;



public class VertexContainerReference extends GraphElementContainerReference<VertexContainer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6397047815119746241L;
	
	
	public VertexContainerReference(VertexContainer container, ReferenceQueue queue) {
		super(container, queue);
	}
	
	
	public VertexContainerReference(VertexContainer container,
			FileChannel input, ReferenceQueue queue) throws IOException, ClassNotFoundException {
		super(container, input, queue);
   		container.vertices = new Vertex[DiskStorageManager.CONTAINER_SIZE];
	}
	
	public VertexContainerReference(VertexContainer container, FileChannel input, VertexContainerReference oldRef, ReferenceQueue queue) throws IOException {
		super(container, input, oldRef, queue);
   		container.vertices = new Vertex[DiskStorageManager.CONTAINER_SIZE];
	}
	
	public VertexContainerReference(VertexContainer container, VertexContainerReference oldRef, ReferenceQueue queue) {
		super(container, oldRef, queue);
   		container.vertices = new Vertex[DiskStorageManager.CONTAINER_SIZE];
	}
	
	public String toString() {
		return "VertexStorage " + id;
	}





	
}
