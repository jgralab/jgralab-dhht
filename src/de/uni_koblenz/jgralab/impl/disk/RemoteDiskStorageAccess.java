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
	
	public long getSigmaIdOfVertexId(int localElementId);
	
	public void setSigmaIdOfVertexId(int localElementId, long globalSigmaId);

	public int getKappaOfVertexId(int localIncidenceId);

	public void setKappaOfVertexId(int localIncidenceId, int kappa);
	
	public long getSigmaIdOfEdgeId(int localElementId);
	
	public void setSigmaIdOfEdgeId(int localElementId, long globalSigmaId);

	public int getKappaOfEdgeId(int localIncidenceId);

	public void setKappaOfEdgeId(int localIncidenceId, int kappa);
	
	
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
	
	public long getFirstIncidenceIdAtVertexId(int localElementId);
	
	public void setFirstIncidenceIdAtVertexId(int localElementId, long l);
	
	public long getLastIncidenceIdAtVertexId(int localElementId);

	public void setLastIncidenceIdAtVertexId(int localElementId, long id);
		
	public long getNextIncidenceIdAtVertexId(int convertToLocalId);

	public long getPreviousIncidenceIdAtVertexId(int convertToLocalId);
	
	public void setNextIncidenceAtVertexId(int localIncidenceId, long nextIncId);
	
	public void setPreviousIncidenceAtVertexId(int localIncidenceId, long previousIncId);
	
	public long getConnectedVertexId(int incidenceId);
	
	public void setConnectedVertexId(int convertToLocalId, long vertexId);
	
	public long getIncidenceListVersionOfVertexId(int elementId);
	
	public void increaseIncidenceListVersionOfVertexId(int elementId);
	
	
	public long getFirstIncidenceIdAtEdgeId(int localElementId);
	
	public void setFirstIncidenceIdAtEdgeId(int localElementId, long l);
	
	public long getLastIncidenceIdAtEdgeId(int localElementId);

	public void setLastIncidenceIdAtEdgeId(int localElementId, long id);
	
	public long getNextIncidenceIdAtEdgeId(int convertToLocalId);
	
	public long getPreviousIncidenceIdAtEdgeId(int convertToLocalId);
		
	public void setNextIncidenceAtEdgeId(int localIncidenceId, long nextIncId);
	
	public void setPreviousIncidenceAtEdgeId(int localIncidenceId, long previousIncId);

	public long getConnectedEdgeId(int incidenceId);
	
	public void setConnectedEdgeId(int convertToLocalId, long edgeId);
	
	public long getIncidenceListVersionOfEdgeId(int elementId);
	
	public void increaseIncidenceListVersionOfEdgeId(int elementId);
	
	

	public int getIncidenceTypeId(int localIncidenceId);
	


}
