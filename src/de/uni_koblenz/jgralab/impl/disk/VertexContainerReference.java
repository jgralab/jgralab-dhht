package de.uni_koblenz.jgralab.impl.disk;

import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.nio.channels.FileChannel;

import de.uni_koblenz.jgralab.Vertex;



public class VertexContainerReference extends GraphElementContainerReference<VertexContainer> {

	
	
	public VertexContainerReference(VertexContainer container, ReferenceQueue<VertexContainer> queue) {
		super(container, queue);
	}
	
	
	public VertexContainerReference(VertexContainer container, FileChannel input, ReferenceQueue<VertexContainer> queue) throws IOException, ClassNotFoundException {
		super(container, input, queue);
   		container.vertices = new Vertex[DiskStorageManager.CONTAINER_SIZE];
	}
	
	public VertexContainerReference(VertexContainer container, FileChannel input, VertexContainerReference oldRef, ReferenceQueue<VertexContainer> queue) throws IOException {
		super(container, input, oldRef, queue);
   		container.vertices = new Vertex[DiskStorageManager.CONTAINER_SIZE];
	}
	
	public VertexContainerReference(VertexContainer container, VertexContainerReference oldRef, ReferenceQueue<VertexContainer> queue) {
		super(container, oldRef, queue);
   		container.vertices = new Vertex[DiskStorageManager.CONTAINER_SIZE];
	}
	
	public String toString() {
		return "VertexStorage " + id;
	}





	
}
