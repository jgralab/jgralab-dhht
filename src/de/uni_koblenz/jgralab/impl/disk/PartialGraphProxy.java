package de.uni_koblenz.jgralab.impl.disk;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Vertex;

/**
 * Local Proxy for a remote partial graph
 * @author dbildh
 *
 */
public class PartialGraphProxy implements Graph {
	
	/* id of the partial graph */
	private int id;
	
	private DiskStorageManager storageManager;
	
	
	public PartialGraphProxy(int id, DiskStorageManager storageManager) {
		this.id = id;
		this.storageManager = storageManager;
	}
	
	public Vertex getFirstVertex() {
		int vertexId = 
		return storageManager.getVertex(vertexId);	
	}
	
	

}
