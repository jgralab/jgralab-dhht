package de.uni_koblenz.jgralab.impl.disk;

import java.rmi.Remote;



/**
 * Remote interface of DiskStorage used to access properties of the stored elements
 * @author dbildh
 *
 */
public interface RemoteDiskStorageAccess extends Remote {

	/* ========================================================
	 * Methods to access hierarchy 
	 * ======================================================== */
	
	public long getSigmaId(int localElementId);
	
	public void setSigmaId(int localElementId, long globalSigmaId);

	public int getKappa(int localIncidenceId);

	public void setKappa(int localIncidenceId, int kappa);
	

	
	
	/* ========================================================
	 * Methods to access vertices
	 * ======================================================== */
	
	public int getVertexTypeId(int localVertexId);
	
	
	public long getNextVertexId(int localVertexId);
	
	public void setNextVertexId(int localVertexId, long nextVertexId);
	
	public long getPreviousVertexId(int localVertexId);
	
	public void setPreviousVertexId(int localVertexId, long previousVertexId);
	
	/* ========================================================
	 * Methods to access edges
	 * ======================================================== */
	
	public int getEdgeTypeId(int localEdgeId);
	
	public long getNextEdgeId(int localEdgeId);
	
	public void setNextEdgeId(int localEdgeId, long nextEdgeId);
	
	public long getPreviousEdgeId(int localEdgeId);
	
	public void setPreviousEdgeId(int localEdgeId, long previousEdgeId);
	
	
	/* ========================================================
	 * Methods to access incidences 
	 * ======================================================== */
	
	public long getFirstIncidenceId(int localElementId);
	
	public void setFirstIncidenceId(int localElementId, long l);
	
	public long getLastIncidenceId(int localElementId);

	public void setLastIncidenceId(int localElementId, long id);
	

	
	public long getNextIncidenceIdAtVertexId(int convertToLocalId);

	public long getNextIncidenceIdAtEdgeId(int convertToLocalId);
	
	public long getPreviousIncidenceIdAtVertexId(int convertToLocalId);

	public long getPreviousIncidenceIdAtEdgeId(int convertToLocalId);
	
	public void setNextIncidenceAtVertexId(int localIncidenceId, long nextIncId);
	
	public void setNextIncidenceAtEdgeId(int localIncidenceId, long nextIncId);
	
	public void setPreviousIncidenceAtVertexId(int localIncidenceId, long previousIncId);
	
	public void setPreviousIncidenceAtEdgeId(int localIncidenceId, long previousIncId);

	public int getIncidenceTypeId(int localIncidenceId);

	public long getConnectedVertexId(int incidenceId);
	
	public long getConnectedEdgeId(int incidenceId);

	public long getIncidenceListVersion(int elementId);
	
	public void increaseIncidenceListVersion(int elementId);

	public void setIncidenceVertexId(int convertToLocalId, long vertexId);

	public void setIncidenteEdgeId(int convertToLocalId, long edgeId);






}
