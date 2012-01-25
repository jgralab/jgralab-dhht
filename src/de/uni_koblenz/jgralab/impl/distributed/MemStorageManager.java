package de.uni_koblenz.jgralab.impl.distributed;


import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.impl.RemoteStorageAccess;

/**
 * This class realizes the storage of vertices, edges and incidences on the
 * disk. All methods may be used only with local objects and local ids.
 * 
 * @author dbildh
 * 
 */
public final class MemStorageManager implements RemoteStorageAccess {

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
		//this.graphDatabase = database;
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
			de.uni_koblenz.jgralab.impl.distributed.VertexImpl v) {
		vertexArray[v.getLocalId()] = v;
	}


	public void removeVertexFromStorage(int convertToLocalId) {
		vertexArray[convertToLocalId] = null;
	}

	public void storeEdge(EdgeImpl e) {
		edgeArray[e.getLocalId()] = e;
	}
	
	
	public void removeEdgeFromStorage(int convertToLocalId) {
		edgeArray[convertToLocalId] = null;
	}
	
	
	public void storeIncidence(IncidenceImpl i) {
		incidenceArray[i.getLocalId()] = i;
	}

	public void removeIncidenceFromStorage(int convertToLocalId) {
		incidenceArray[convertToLocalId] = null;
	}

	@Override
	public long getSigmaIdOfVertexId(int localElementId) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setSigmaIdOfVertexId(int localElementId, long globalSigmaId)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public int getKappaOfVertexId(int localIncidenceId) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setKappaOfVertexId(int localIncidenceId, int kappa)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public long getSigmaIdOfEdgeId(int localElementId) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setSigmaIdOfEdgeId(int localElementId, long globalSigmaId)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public int getKappaOfEdgeId(int localIncidenceId) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setKappaOfEdgeId(int localIncidenceId, int kappa)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public int getVertexTypeId(int localVertexId) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public long getNextVertexId(int localVertexId) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setNextVertexId(int localVertexId, long nextVertexId)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public long getPreviousVertexId(int localVertexId) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setPreviousVertexId(int localVertexId, long previousVertexId)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public int getEdgeTypeId(int localEdgeId) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public long getNextEdgeId(int localEdgeId) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setNextEdgeId(int localEdgeId, long nextEdgeId)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public long getPreviousEdgeId(int localEdgeId) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setPreviousEdgeId(int localEdgeId, long previousEdgeId)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public long getFirstIncidenceIdAtVertexId(int localElementId)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setFirstIncidenceIdAtVertexId(int localElementId, long id)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public long getLastIncidenceIdAtVertexId(int localElementId)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setLastIncidenceIdAtVertexId(int localElementId, long id)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public long getNextIncidenceIdAtVertexId(int convertToLocalId)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public long getPreviousIncidenceIdAtVertexId(int convertToLocalId)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setNextIncidenceAtVertexId(int localIncidenceId, long nextIncId)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void setPreviousIncidenceAtVertexId(int localIncidenceId,
			long previousIncId) throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public long getConnectedVertexId(int incidenceId) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public long getIncidenceListVersionOfVertexId(int elementId)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void increaseIncidenceListVersionOfVertexId(int elementId)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public long getFirstIncidenceIdAtEdgeId(int localElementId)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setFirstIncidenceIdAtEdgeId(int localElementId, long l)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public long getLastIncidenceIdAtEdgeId(int localElementId)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setLastIncidenceIdAtEdgeId(int localElementId, long id)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public long getNextIncidenceIdAtEdgeId(int convertToLocalId)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public long getPreviousIncidenceIdAtEdgeId(int convertToLocalId)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void setNextIncidenceAtEdgeId(int localIncidenceId, long nextIncId)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void setPreviousIncidenceAtEdgeId(int localIncidenceId,
			long previousIncId) throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public long getConnectedEdgeId(int incidenceId) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public long getIncidenceListVersionOfEdgeId(int elementId)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public void increaseIncidenceListVersionOfEdgeId(int elementId)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public int getIncidenceTypeId(int localIncidenceId) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}






}
