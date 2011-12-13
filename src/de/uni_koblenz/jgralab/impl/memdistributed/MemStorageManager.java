package de.uni_koblenz.jgralab.impl.memdistributed;


import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;

/**
 * This class realizes the storage of vertices, edges and incidences on the
 * disk. All methods may be used only with local objects and local ids.
 * 
 * @author dbildh
 * 
 */
public final class MemStorageManager {

	private final GraphDatabaseBaseImpl graphDatabase;
	 
	/**
	 * free index list for vertices
	 */
	private FreeIndexList freeVertexList;

	/**
	 * array of vertices
	 */
	private VertexImpl[] vertexArray;
	
	/**
	 * free index list for edges
	 */
	protected FreeIndexList freeEdgeList;

	/**
	 * array of incidences
	 */
	private EdgeImpl[] edgeArray;
	

	/**
	 * free index list for vertices
	 */
	protected FreeIndexList freeIncidenceList;

	/**
	 * array of incidences
	 */
	private IncidenceImpl[] incidenceArray;
	
	
	

	public MemStorageManager(GraphDatabaseBaseImpl database) {
		this.graphDatabase = database;
	}



	public final Vertex getVertexObject(int id) {
		return vertexArray[id];
	}


	public final Edge getEdgeObject(int id) {
		return edgeArray[id];
	}
	

	public final Incidence getIncidenceObject(int id) {
		return incidenceArray[id];
	}


	private int getLocalId(long id) {
		return GraphDatabaseBaseImpl.convertToLocalId(id);
	}



	public void storeVertex(
			de.uni_koblenz.jgralab.impl.memdistributed.VertexImpl v) {
		vertexArray[v.getLocalId()] = v;
	}


}
