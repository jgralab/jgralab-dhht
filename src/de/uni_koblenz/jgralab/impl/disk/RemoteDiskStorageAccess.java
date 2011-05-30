package de.uni_koblenz.jgralab.impl.disk;

import java.rmi.Remote;

import de.uni_koblenz.jgralab.Direction;

/**
 * Remote interface of DiskStorage used to access properties of the stored elements
 * @author dbildh
 *
 */
public interface RemoteDiskStorageAccess extends Remote {

	public long getNextVertexId(int localVertexId);
	
	public void setNextVertexId(int localVertexId, long nextVertexId);
	
	public long getPreviousVertexId(int localVertexId);
	
	public void setPreviousVertexId(int localVertexId, long previousVertexId);
	
	
	public long getNextEdgeId(int localEdgeId);
	
	public void setNextEdgeId(int localEdgeId, long nextEdgeId);
	
	public long getPreviousEdgeId(int localEdgeId);
	
	public void setPreviousEdgeId(int localEdgeId, long previousEdgeId);
	
	
	public long getFirstIncidenceId(int localElementId);
	
	public void setFirstIncidenceId(int localElementId, long l);
	
	public long getLastIncidenceId(int localElementId);

	public void setLastIncidenceId(int localElementId, long id);
	

	public void setNextIncidenceAtVertexId(int localIncidenceId, long nextIncId);
	
	public void setNextIncidenceAtEdgeId(int localIncidenceId, long nextIncId);
	
	public void setPreviousIncidenceAtVertexId(int localIncidenceId, long previousIncId);
	
	public void setPreviousIncidenceAtEdgeId(int localIncidenceId, long previousIncId);

	
	public long getSigmaId(int localElementId);
	
	public void setSigmaId(int localElementId, long globalSigmaId);

	public int getKappa(int localIncidenceId);

	public void setKappa(int localIncidenceId, int kappa);
	
	public int getVertexTypeId(int localVertexId);
	
	public int getEdgeTypeId(int localEdgeId);
	
	public int getIncidenceTypeId(int localIncidenceId);

	public long getConnectedVertexId(int incidenceId);
	
	public long getConnectedEdgeId(int incidenceId);

	public long getIncidenceListVersion(int elementId);
	
	public void increaseIncidenceListVersion(int elementId);

}
