package de.uni_koblenz.jgralab.impl.disk;

import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.nio.channels.FileChannel;

import de.uni_koblenz.jgralab.Edge;



public class EdgeContainerReference extends GraphElementContainerReference<EdgeContainer> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4021980277985504150L;

	long[] incidenceListVersion = null;
	
	int[] kappa = null;
	
	int[] sigmaId = null;
	
	int[] nextElementInGraphId = null;
	
	int[] previousElementInGraphId = null;
	
	int[] firstIncidenceId = null;
	
	int[] lastIncidenceId = null;
	
	
	
	/* creates a new reference to a freshly created container */
	public EdgeContainerReference(EdgeContainer container, ReferenceQueue queue) {
		super(container, queue);
	}
		
	public EdgeContainerReference(EdgeContainer container,
			FileChannel input, ReferenceQueue  queue) throws IOException, ClassNotFoundException {
		super(container, input, queue);
   		container.edges = new Edge[DiskStorageManager.CONTAINER_SIZE];
	}
	
	public EdgeContainerReference(EdgeContainer container, FileChannel input, EdgeContainerReference oldRef, ReferenceQueue queue) throws IOException {
		super(container, input, oldRef, queue);
   		container.edges = new Edge[DiskStorageManager.CONTAINER_SIZE];
	}
	
	public EdgeContainerReference(EdgeContainer container, EdgeContainerReference oldRef, ReferenceQueue queue) {
		super(container, oldRef, queue);
   		container.edges = new Edge[DiskStorageManager.CONTAINER_SIZE];
	}
		
		
	public String toString() {
		return "EdgeStorage " + id;
	}



	
}
