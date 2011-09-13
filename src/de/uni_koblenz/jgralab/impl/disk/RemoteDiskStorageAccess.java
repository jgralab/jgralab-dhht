package de.uni_koblenz.jgralab.impl.disk;

import java.rmi.Remote;

/**
 * Remote interface of DiskStorage used to access properties of the stored
 * elements
 * 
 * @author dbildh
 * 
 */
public interface RemoteDiskStorageAccess extends Remote {

	/*
	 * ======================================================== Methods to
	 * access hierarchy ========================================================
	 */

	public long getSigmaIdOfVertexId(int localElementId)
			throws RuntimeException;

	public void setSigmaIdOfVertexId(int localElementId, long globalSigmaId)
			throws RuntimeException;

	public int getKappaOfVertexId(int localIncidenceId) throws RuntimeException;

	public void setKappaOfVertexId(int localIncidenceId, int kappa)
			throws RuntimeException;

	public long getSigmaIdOfEdgeId(int localElementId) throws RuntimeException;

	public void setSigmaIdOfEdgeId(int localElementId, long globalSigmaId)
			throws RuntimeException;

	public int getKappaOfEdgeId(int localIncidenceId) throws RuntimeException;

	public void setKappaOfEdgeId(int localIncidenceId, int kappa)
			throws RuntimeException;

	/*
	 * ======================================================== Methods to
	 * access vertices ========================================================
	 */

	public int getVertexTypeId(int localVertexId) throws RuntimeException;

	public long getNextVertexId(int localVertexId) throws RuntimeException;

	public void setNextVertexId(int localVertexId, long nextVertexId)
			throws RuntimeException;

	public long getPreviousVertexId(int localVertexId) throws RuntimeException;

	public void setPreviousVertexId(int localVertexId, long previousVertexId)
			throws RuntimeException;

	/*
	 * ======================================================== Methods to
	 * access edges ========================================================
	 */

	public int getEdgeTypeId(int localEdgeId) throws RuntimeException;

	public long getNextEdgeId(int localEdgeId) throws RuntimeException;

	public void setNextEdgeId(int localEdgeId, long nextEdgeId)
			throws RuntimeException;

	public long getPreviousEdgeId(int localEdgeId) throws RuntimeException;

	public void setPreviousEdgeId(int localEdgeId, long previousEdgeId)
			throws RuntimeException;

	/*
	 * ======================================================== Methods to
	 * access incidences
	 * ========================================================
	 */

	public long getFirstIncidenceIdAtVertexId(int localElementId)
			throws RuntimeException;

	public void setFirstIncidenceIdAtVertexId(int localElementId, long id)
			throws RuntimeException;

	public long getLastIncidenceIdAtVertexId(int localElementId)
			throws RuntimeException;

	public void setLastIncidenceIdAtVertexId(int localElementId, long id)
			throws RuntimeException;

	public long getNextIncidenceIdAtVertexId(int convertToLocalId)
			throws RuntimeException;

	public long getPreviousIncidenceIdAtVertexId(int convertToLocalId)
			throws RuntimeException;

	public void setNextIncidenceAtVertexId(int localIncidenceId, long nextIncId)
			throws RuntimeException;

	public void setPreviousIncidenceAtVertexId(int localIncidenceId,
			long previousIncId) throws RuntimeException;

	public long getConnectedVertexId(int incidenceId) throws RuntimeException;

	public long getIncidenceListVersionOfVertexId(int elementId)
			throws RuntimeException;

	public void increaseIncidenceListVersionOfVertexId(int elementId)
			throws RuntimeException;

	public long getFirstIncidenceIdAtEdgeId(int localElementId)
			throws RuntimeException;

	public void setFirstIncidenceIdAtEdgeId(int localElementId, long l)
			throws RuntimeException;

	public long getLastIncidenceIdAtEdgeId(int localElementId)
			throws RuntimeException;

	public void setLastIncidenceIdAtEdgeId(int localElementId, long id)
			throws RuntimeException;

	public long getNextIncidenceIdAtEdgeId(int convertToLocalId)
			throws RuntimeException;

	public long getPreviousIncidenceIdAtEdgeId(int convertToLocalId)
			throws RuntimeException;

	public void setNextIncidenceAtEdgeId(int localIncidenceId, long nextIncId)
			throws RuntimeException;

	public void setPreviousIncidenceAtEdgeId(int localIncidenceId,
			long previousIncId) throws RuntimeException;

	public long getConnectedEdgeId(int incidenceId) throws RuntimeException;

	public long getIncidenceListVersionOfEdgeId(int elementId)
			throws RuntimeException;

	public void increaseIncidenceListVersionOfEdgeId(int elementId)
			throws RuntimeException;

	public int getIncidenceTypeId(int localIncidenceId) throws RuntimeException;

}
