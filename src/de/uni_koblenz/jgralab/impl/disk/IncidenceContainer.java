package de.uni_koblenz.jgralab.impl.disk;

import de.uni_koblenz.jgralab.Incidence;

public class IncidenceContainer extends StorageContainer {
	
	public IncidenceContainer(int id, int size, DiskStorageManager backgroundStorage) {
		super(id, size, backgroundStorage);
		vertexId = new long[size];
		edgeId = new long[size];
		nextIncidenceAtVertexId = new long[size];
		previousIncidenceAtVertexId = new long[size];
		nextIncidenceAtEdgeId = new long[size];
		previousIncidenceAtEdgeId = new long[size];
		direction = new boolean[size];
		incidences = new Incidence[size];
	}
	
	public IncidenceContainer(int id, DiskStorageManager backgroundStorage) {
		this.id = id;
		this.backgroundStorage = backgroundStorage;
	}
	
	
	long[] vertexId;
	
	long[] edgeId;
	
	long[] nextIncidenceAtVertexId;
	
	long[] previousIncidenceAtVertexId;

	long[] nextIncidenceAtEdgeId;
	
	long[] previousIncidenceAtEdgeId;
	
	boolean[] direction;
	
	transient Incidence[] incidences;


	
}
