package de.uni_koblenz.jgralab.impl.disk;

import de.uni_koblenz.jgralab.Incidence;

public class IncidenceContainer extends StorageContainer {
	
	public IncidenceContainer(int id, int size, BackgroundStorage backgroundStorage) {
		super(id, size, backgroundStorage);
		vertexId = new int[size];
		edgeId = new int[size];
		nextIncidenceAtVertexId = new int[size];
		previousIncidenceAtVertexId = new int[size];
		nextIncidenceAtEdgeId = new int[size];
		previousIncidenceAtEdgeId = new int[size];
		direction = new boolean[size];
		incidences = new Incidence[size];
	}
	
	public IncidenceContainer(int id, BackgroundStorage backgroundStorage) {
		this.id = id;
		this.backgroundStorage = backgroundStorage;
	}
	
	
	int[] vertexId;
	
	int[] edgeId;
	
	int[] nextIncidenceAtVertexId;
	
	int[] previousIncidenceAtVertexId;

	int[] nextIncidenceAtEdgeId;
	
	int[] previousIncidenceAtEdgeId;
	
	boolean[] direction;
	
	transient Incidence[] incidences;


	
}
