package de.uni_koblenz.jgralab.impl.distributed;


import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.RemoteStorageAccess;
import de.uni_koblenz.jgralab.impl.distributed.EdgeImpl;
import de.uni_koblenz.jgralab.impl.mem.FreeIndexList;
import de.uni_koblenz.jgralab.impl.distributed.IncidenceImpl;
import de.uni_koblenz.jgralab.impl.distributed.VertexImpl;

/**
 * This class realizes the storage of vertices, edges and incidences on the
 * disk. All methods may be used only with local objects and local ids.
 * 
 * @author dbildh
 * 
 */
public final class MemStorageManager implements RemoteStorageAccess {

	
	private static final double EXPAND_FACTOR = 1.5;

	private static int INITIAL_ELEMENT_ARRAY_SIZE = 1000000;
	
	private static int INITIAL_INCIDENCE_ARRAY_SIZE = 3000000;
	
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

	private GraphDatabaseBaseImpl graphDatabase;
	
	
	public MemStorageManager(GraphDatabaseBaseImpl database) {
		this.graphDatabase = database;
		expandVertexArray(INITIAL_ELEMENT_ARRAY_SIZE);
		expandEdgeArray(INITIAL_ELEMENT_ARRAY_SIZE);
		expandIncidenceArray(INITIAL_INCIDENCE_ARRAY_SIZE);
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


//	private int getLocalId(long id) {
//		return GraphDatabaseBaseImpl.convertToLocalId(id);
//	}



	public void storeVertex(
			de.uni_koblenz.jgralab.impl.distributed.VertexImpl v) {
		if (v.getLocalId() >= vertexArray.length)
			expandVertexArray((int) (vertexArray.length * EXPAND_FACTOR));
		vertexArray[v.getLocalId()] = v;
	}


	public void removeVertexFromStorage(int convertToLocalId) {
		vertexArray[convertToLocalId] = null;
	}

	public void storeEdge(EdgeImpl e) {
		if (e.getLocalId() >= edgeArray.length)
			expandEdgeArray((int) (edgeArray.length * EXPAND_FACTOR));
		edgeArray[e.getLocalId()] = e;
	}
	
	
	public void removeEdgeFromStorage(int convertToLocalId) {
		edgeArray[convertToLocalId] = null;
	}
	
	
	public void storeIncidence(IncidenceImpl i) {
		if (i.getLocalId() >= incidenceArray.length)
			expandIncidenceArray((int) (incidenceArray.length * EXPAND_FACTOR));
		incidenceArray[i.getLocalId()] = i;
	}

	public void removeIncidenceFromStorage(int convertToLocalId) {
		incidenceArray[convertToLocalId] = null;
	}

	@Override
	public long getSigmaIdOfVertexId(int localElementId) throws RemoteException {
		return ((VertexImpl) getVertexObject(localElementId)).getSigmaId();
	}



	@Override
	public void setSigmaIdOfVertexId(int localElementId, long globalSigmaId)
			throws RemoteException {
		((VertexImpl) getVertexObject(localElementId)).setSigmaId(globalSigmaId);
	}



	@Override
	public int getKappaOfVertexId(int localElementId) throws RemoteException {
		return ((VertexImpl) getVertexObject(localElementId)).getKappa();
	}



	@Override
	public void setKappaOfVertexId(int localElementId, int kappa)
			throws RemoteException {
		 ((VertexImpl) getVertexObject(localElementId)).setKappa(kappa);
	}



	@Override
	public long getSigmaIdOfEdgeId(int localElementId) throws RemoteException {
		return ((EdgeImpl) getEdgeObject(localElementId)).getSigmaId();
	}



	@Override
	public void setSigmaIdOfEdgeId(int localElementId, long globalSigmaId)
			throws RemoteException {
		((EdgeImpl) getEdgeObject(localElementId)).setSigmaId(globalSigmaId);
	}



	@Override
	public int getKappaOfEdgeId(int localElementId) throws RemoteException {
		return ((EdgeImpl) getEdgeObject(localElementId)).getKappa();
	}



	@Override
	public void setKappaOfEdgeId(int localElementId, int kappa)
			throws RemoteException {
		((EdgeImpl) getEdgeObject(localElementId)).setKappa(kappa);
	}



	@Override
	public int getVertexTypeId(int localVertexId) throws RemoteException {
		return ((VertexImpl) getVertexObject(localVertexId)).getType().getId();
	}



	@Override
	public long getNextVertexId(int localVertexId) throws RemoteException {
		return ((VertexImpl) getVertexObject(localVertexId)).getNextElementId();
	}



	@Override
	public void setNextVertexId(int localVertexId, long nextVertexId)
			throws RemoteException {
		((VertexImpl) getVertexObject(localVertexId)).setNextElementId(nextVertexId);
	}



	@Override
	public long getPreviousVertexId(int localVertexId) throws RemoteException {
		return ((VertexImpl) getVertexObject(localVertexId)).getPreviousElementId();
	}



	@Override
	public void setPreviousVertexId(int localVertexId, long previousVertexId)
			throws RemoteException {
		((VertexImpl) getVertexObject(localVertexId)).setNextElementId(previousVertexId);
	}



	@Override
	public int getEdgeTypeId(int localEdgeId) throws RemoteException {
		return ((EdgeImpl) getEdgeObject(localEdgeId)).getType().getId();
	}



	@Override
	public long getNextEdgeId(int localEdgeId) throws RemoteException {
		return ((EdgeImpl) getEdgeObject(localEdgeId)).getNextElementId();
	}



	@Override
	public void setNextEdgeId(int localEdgeId, long nextEdgeId)
			throws RemoteException {
		((EdgeImpl) getEdgeObject(localEdgeId)).setNextElementId(nextEdgeId);
	}



	@Override
	public long getPreviousEdgeId(int localEdgeId) throws RemoteException {
		return ((EdgeImpl) getEdgeObject(localEdgeId)).getPreviousElementId();
	}



	@Override
	public void setPreviousEdgeId(int localEdgeId, long previousEdgeId)
			throws RemoteException {
		((EdgeImpl) getEdgeObject(localEdgeId)).setNextElementId(previousEdgeId);
	}




	@Override
	public long getFirstIncidenceIdAtVertexId(int localVertexId)
			throws RemoteException {
		return ((VertexImpl) getVertexObject(localVertexId)).getFirstIncidenceId();
	}



	@Override
	public void setFirstIncidenceIdAtVertexId(int localVertexId, long incidenceId)
			throws RemoteException {
		((VertexImpl) getVertexObject(localVertexId)).setFirstIncidenceId(incidenceId);
	}



	@Override
	public long getLastIncidenceIdAtVertexId(int localVertexId)
			throws RemoteException {
		return ((VertexImpl) getVertexObject(localVertexId)).getLastIncidenceId();
	}



	@Override
	public void setLastIncidenceIdAtVertexId(int localVertexId, long incidenceId)
			throws RemoteException {
		((VertexImpl) getVertexObject(localVertexId)).setLastIncidenceId(incidenceId);
	}



	@Override
	public long getNextIncidenceIdAtVertexId(int incidenceId)
			throws RemoteException {
		return ((IncidenceImpl) getIncidenceObject(incidenceId)).getNextIncidenceIdAtVertex();
	}



	@Override
	public long getPreviousIncidenceIdAtVertexId(int incidenceId)
			throws RemoteException {
		return ((IncidenceImpl) getIncidenceObject(incidenceId)).getPreviousIncidenceIdAtVertex();
	}



	@Override
	public void setNextIncidenceAtVertexId(int incidenceId, long nextIncId)
			throws RemoteException {
		((IncidenceImpl) getIncidenceObject(incidenceId)).setNextIncidenceIdAtVertex(nextIncId);
	}



	@Override
	public void setPreviousIncidenceAtVertexId(int incidenceId,	long previousIncId) throws RemoteException {
		((IncidenceImpl) getIncidenceObject(incidenceId)).setPreviousIncidenceIdAtVertex(previousIncId);
	}



	@Override
	public long getConnectedVertexId(int incidenceId) throws RemoteException {
		return ((IncidenceImpl) getIncidenceObject(incidenceId)).getIncidentVertexId();
	}



	@Override
	public long getIncidenceListVersionOfVertexId(int vertexId)
			throws RemoteException {
		return ((VertexImpl) getVertexObject(vertexId)).getIncidenceListVersion();
	}



	@Override
	public void increaseIncidenceListVersionOfVertexId(int vertexId)
			throws RemoteException {
		((VertexImpl) getVertexObject(vertexId)).increaseIncidenceListVersion();
		
	}



	@Override
	public long getFirstIncidenceIdAtEdgeId(int localEdgeId)
			throws RemoteException {
		return ((EdgeImpl) getEdgeObject(localEdgeId)).getFirstIncidenceId();
	}



	@Override
	public void setFirstIncidenceIdAtEdgeId(int localEdgeId, long incidenceId)
			throws RemoteException {
		((EdgeImpl) getEdgeObject(localEdgeId)).setFirstIncidenceId(incidenceId);
	}



	@Override
	public long getLastIncidenceIdAtEdgeId(int localEdgeId)
			throws RemoteException {
		return ((EdgeImpl) getEdgeObject(localEdgeId)).getLastIncidenceId();
	}



	@Override
	public void setLastIncidenceIdAtEdgeId(int localEdgeId, long incidenceId)
			throws RemoteException {
		((EdgeImpl) getEdgeObject(localEdgeId)).setLastIncidenceId(incidenceId);
	}



	@Override
	public long getNextIncidenceIdAtEdgeId(int incidenceId)
			throws RemoteException {
		return ((IncidenceImpl) getIncidenceObject(incidenceId)).getNextIncidenceIdAtEdge();
	}



	@Override
	public long getPreviousIncidenceIdAtEdgeId(int incidenceId)
			throws RemoteException {
		return ((IncidenceImpl) getIncidenceObject(incidenceId)).getPreviousIncidenceIdAtEdge();
	}



	@Override
	public void setNextIncidenceAtEdgeId(int incidenceId, long nextIncId)
			throws RemoteException {
		((IncidenceImpl) getIncidenceObject(incidenceId)).setNextIncidenceIdAtEdge(nextIncId);
	}



	@Override
	public void setPreviousIncidenceAtEdgeId(int incidenceId,	long previousIncId) throws RemoteException {
		((IncidenceImpl) getIncidenceObject(incidenceId)).setPreviousIncidenceIdAtEdge(previousIncId);
	}



	@Override
	public long getConnectedEdgeId(int incidenceId) throws RemoteException {
		return ((IncidenceImpl) getIncidenceObject(incidenceId)).getIncidentEdgeId();
	}



	@Override
	public long getIncidenceListVersionOfEdgeId(int edgeId)
			throws RemoteException {
		return ((EdgeImpl) getEdgeObject(edgeId)).getIncidenceListVersion();
	}



	@Override
	public void increaseIncidenceListVersionOfEdgeId(int edgeId)
			throws RemoteException {
		((EdgeImpl) getEdgeObject(edgeId)).increaseIncidenceListVersion();

	}



	@Override
	public int getIncidenceTypeId(int localIncidenceId) throws RemoteException {
		return ((IncidenceImpl)getIncidenceObject(localIncidenceId)).getType().getId();
	}




	
	/**
	 * Changes the size of the edge array of this graph to newSize.
	 * 
	 * @param newSize
	 *            the new size of the edge array
	 */
	protected void expandEdgeArray(int newSize) {
		int eMax = 0;
		if (edgeArray != null)
			eMax = edgeArray.length;
		if (newSize <= eMax) {
			throw new GraphException("newSize must be > eSize: eSize=" + eMax
					+ ", newSize=" + newSize);
		}

		EdgeImpl[] e = new EdgeImpl[newSize + 1];
		if (edgeArray != null) {
			System.arraycopy(edgeArray, 0, e, 0, edgeArray.length);
		}
		edgeArray = e;

		if (freeEdgeList == null) {
			freeEdgeList = new FreeIndexList(newSize);
		} else {
			freeEdgeList.expandBy(newSize - eMax);
		}
	}


	/**
	 * Changes the size of the incidence array of this graph to newSize.
	 * 
	 * @param newSize
	 *            the new size of the incidence array
	 */
	protected void expandIncidenceArray(int newSize) {
		int iMax = 0;
		if (incidenceArray != null)
			iMax = incidenceArray.length;
		if (newSize <= iMax) {
			throw new GraphException("newSize must > iSize: iSize=" + iMax
					+ ", newSize=" + newSize);
		}
		IncidenceImpl[] expandedArray = new IncidenceImpl[newSize + 1];
		if (incidenceArray != null) {
			System.arraycopy(incidenceArray, 0, expandedArray, 0,
					incidenceArray.length);
		}
		if (freeIncidenceList == null) {
			freeIncidenceList = new FreeIndexList(newSize);
		} else {
			freeIncidenceList.expandBy(newSize - iMax);
		}
		incidenceArray = expandedArray;
	}

	/**
	 * Changes the size of the vertex array of this graph to newSize.
	 * 
	 * @param newSize
	 *            the new size of the vertex array
	 */
	protected void expandVertexArray(int newSize) {
		int vMax = 0;
		if (vertexArray != null)
			vMax = vertexArray.length;
		if (newSize <= vMax) {
			throw new GraphException("newSize must > vSize: vSize=" + vMax
					+ ", newSize=" + newSize);
		}
		VertexImpl[] expandedArray = new VertexImpl[newSize + 1];
		if (vertexArray != null) {
			System.arraycopy(vertexArray, 0, expandedArray, 0,
					vertexArray.length);
		}
		if (freeVertexList == null) {
			freeVertexList = new FreeIndexList(newSize);
		} else {
			freeVertexList.expandBy(newSize - vMax);
		}
		vertexArray = expandedArray;
	}


}
