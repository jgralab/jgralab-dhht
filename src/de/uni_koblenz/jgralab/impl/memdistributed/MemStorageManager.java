package de.uni_koblenz.jgralab.impl.memdistributed;


import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphFactory;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.RemoteStorageAccess;
import de.uni_koblenz.jgralab.impl.mem.EdgeImpl;
import de.uni_koblenz.jgralab.impl.mem.FreeIndexList;
import de.uni_koblenz.jgralab.impl.mem.IncidenceImpl;
import de.uni_koblenz.jgralab.impl.mem.VertexImpl;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * This class realizes the storage of vertices, edges and incidences on the
 * disk. All methods may be used only with local objects and local ids.
 * 
 * @author dbildh
 * 
 */
public final class MemStorageManager implements RemoteStorageAccess {

	private final Schema schema;
	
	private final GraphFactory factory;

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
	
	private long[] nextEdgeId;
	
	private long[] previousEdgeId;

	/**
	 * free index list for vertices
	 */
	protected FreeIndexList freeIncidenceList;

	/**
	 * array of incidences
	 */
	private IncidenceImpl[] incidenceArray;
	
	
	private long[] nextIncidenceAtVertexId;
	
	private long[] previousIncidenceAtVertexId;
	

	
	private long[] firstIncidenceAtEdgeId;
	
	private long[] lastIncidenceAtEdgeId;
	
	private long[] nextIncidenceAtEdgeId;
	
	private long[] previousIncidenceAtEdgeId;
	
	private long[] incidenceListVersionOfEdge;
	
	private long[] connectedVertexId;
	
	private long[] connectedEdgeId;
	
	
	private long[] sigmaIdOfEdge;

	
	private int[] kappaOfEdge;

	public MemStorageManager(GraphDatabaseBaseImpl database)
			throws FileNotFoundException {
		schema = database.getSchema();
		this.graphDatabase = database;
		this.factory = database.getGraphFactory();

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


	/*
	 * Methods to access and modify Vseq, Eseq and Iseq
	 */

	// VSeq

	public void setNextVertexId(int vId, long nextVId) {
		nextVertexId[vId] = nextVId;
	}

	public long getNextVertexId(int vId) {
		return nextVertexId[vId];
	}

	public void setPreviousVertexId(int vId, long previousVId) {
		previousVertexId[vId] = previousVId;
	}

	public long getPreviousVertexId(int vId) {
		return previousVertexId[vId];
	}

	// Eseq

	public void setNextEdgeId(int eId, long nextEId) {
		nextEdgeId[eId] = nextEId;
	}

	public long getNextEdgeId(int eId) {
		return nextEdgeId[eId];
	}

	public void setPreviousEdgeId(int eId, long previousEId) {
		previousEdgeId[eId] = previousEId;
	}

	public long getPreviousEdgeId(int eId) {
		return previousEdgeId[eId];
	}

	// Iseq at vertices

	@Override
	public long getFirstIncidenceIdAtVertexId(int vertexId) {
		return firstIncidenceAtVertexId[vertexId];
	}

	@Override
	public void setFirstIncidenceIdAtVertexId(int vertexId, long incidenceId) {
		firstIncidenceAtVertexId[vertexId] = incidenceId;
	}

	@Override
	public long getLastIncidenceIdAtVertexId(int vertexId) {
		return lastIncidenceAtVertexId[vertexId];
	}

	@Override
	public void setLastIncidenceIdAtVertexId(int vertexId, long incidenceId) {
		lastIncidenceAtVertexId[vertexId] = incidenceId;
	}

	@Override
	public long getNextIncidenceIdAtVertexId(int localIncidenceId) {
		return nextIncidenceAtVertexId[localIncidenceId];
	}

	@Override
	public void setNextIncidenceAtVertexId(int localIncidenceId,
			long nextIncidenceId) {
		nextIncidenceAtVertexId[localIncidenceId] = nextIncidenceId;
	}

	@Override
	public long getPreviousIncidenceIdAtVertexId(int localIncidenceId) {
		return previousIncidenceAtVertexId[localIncidenceId];
	}

	@Override
	public void setPreviousIncidenceAtVertexId(int localIncidenceId,
			long previousIncidenceId) {
		previousIncidenceAtVertexId[localIncidenceId] = previousIncidenceId;
	}

	@Override
	public long getIncidenceListVersionOfVertexId(int elemId) {
		return incidenceListVersionOfVertex[elemId];
	}


	@Override
	public void increaseIncidenceListVersionOfVertexId(int elemId) {
		incidenceListVersionOfVertex[elemId]++;
	}

	@Override
	public long getConnectedVertexId(int incidenceId) {
		return connectedVertexId[incidenceId];
	}

	@Override
	public long getFirstIncidenceIdAtEdgeId(int EdgeId) {
		return firstIncidenceAtEdgeId[EdgeId];
	}

	@Override
	public void setFirstIncidenceIdAtEdgeId(int EdgeId, long incidenceId) {
		firstIncidenceAtEdgeId[EdgeId] = incidenceId;
	}

	@Override
	public long getLastIncidenceIdAtEdgeId(int EdgeId) {
		return lastIncidenceAtEdgeId[EdgeId];
	}

	@Override
	public void setLastIncidenceIdAtEdgeId(int EdgeId, long incidenceId) {
		lastIncidenceAtEdgeId[EdgeId] = incidenceId;
	}

	@Override
	public long getNextIncidenceIdAtEdgeId(int localIncidenceId) {
		return nextIncidenceAtEdgeId[localIncidenceId];
	}

	@Override
	public void setNextIncidenceAtEdgeId(int localIncidenceId,
			long nextIncidenceId) {
		nextIncidenceAtEdgeId[localIncidenceId] = nextIncidenceId;
	}

	@Override
	public long getPreviousIncidenceIdAtEdgeId(int localIncidenceId) {
		return previousIncidenceAtEdgeId[localIncidenceId];
	}

	@Override
	public void setPreviousIncidenceAtEdgeId(int localIncidenceId,
			long previousIncidenceId) {
		previousIncidenceAtEdgeId[localIncidenceId] = previousIncidenceId;
	}

	@Override
	public long getIncidenceListVersionOfEdgeId(int elemId) {
		return incidenceListVersionOfEdge[elemId];
	}

	@Override
	public void increaseIncidenceListVersionOfEdgeId(int elemId) {
		incidenceListVersionOfEdge[elemId]++;
	}

	@Override
	public long getConnectedEdgeId(int incidenceId) {
		return connectedEdgeId[incidenceId];
	}

	// hierarchy of vertices

	@Override
	public long getSigmaIdOfVertexId(int localElemId) {
		return sigmaIdOfVertex[localElemId];
	}

	@Override
	public void setSigmaIdOfVertexId(int localElemId, long sigmaId) {
		sigmaIdOfVertex[localElemId] = sigmaId;
	}

	public int getKappaOfVertexId(int localElemId) {
		return kappaOfVertex[localElemId];
	}

	public void setKappaOfVertexId(int localElemId, int kappa) {
		kappaOfVertex[localElemId] = kappa;
	}

	// hierarchy of edges

	@Override
	public long getSigmaIdOfEdgeId(int localElemId) {
		return sigmaIdOfEdge[localElemId];
	}

	@Override
	public void setSigmaIdOfEdgeId(int localElemId, long sigmaId) {
		sigmaIdOfEdge[localElemId] = sigmaId;
	}

	public int getKappaOfEdgeId(int localElemId) {
		return kappaOfEdge[localElemId];
	}

	@Override
	public void setKappaOfEdgeId(int localElemId, int kappa) {
		kappaOfEdge[localElemId] = kappa;
	}

	// types

	public int getVertexTypeId(int localVertexId) {
		return vertexArray[localVertexId].getType().getId();
	}

	public int getEdgeTypeId(int localEdgeId) {
		return edgeArray[localEdgeId].getType().getId();
	}

	public int getIncidenceTypeId(int localIncidenceId) {
		return incidenceArray[localIncidenceId].getType().getId();
	}



}
