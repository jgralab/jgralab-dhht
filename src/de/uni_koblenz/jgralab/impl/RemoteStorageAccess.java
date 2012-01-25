package de.uni_koblenz.jgralab.impl;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface of DiskStorage used to access properties of the stored
 * elements
 * 
 * @author dbildh
 * 
 */
public interface RemoteStorageAccess extends Remote {

	/*
	 * ======================================================== Methods to
	 * access hierarchy ========================================================
	 */

	public long getSigmaIdOfVertexId(int localElementId) throws RemoteException;

	public void setSigmaIdOfVertexId(int localElementId, long globalSigmaId)
			throws RemoteException;

	public int getKappaOfVertexId(int localIncidenceId) throws RemoteException;

	public void setKappaOfVertexId(int localIncidenceId, int kappa)
			throws RemoteException;

	public long getSigmaIdOfEdgeId(int localElementId) throws RemoteException;

	public void setSigmaIdOfEdgeId(int localElementId, long globalSigmaId)
			throws RemoteException;

	public int getKappaOfEdgeId(int localIncidenceId) throws RemoteException;

	public void setKappaOfEdgeId(int localIncidenceId, int kappa)
			throws RemoteException;

	/*
	 * ======================================================== Methods to
	 * access vertices ========================================================
	 */

	public int getVertexTypeId(int localVertexId) throws RemoteException;

	public long getNextVertexId(int localVertexId) throws RemoteException;

	public void setNextVertexId(int localVertexId, long nextVertexId)
			throws RemoteException;

	public long getPreviousVertexId(int localVertexId) throws RemoteException;

	public void setPreviousVertexId(int localVertexId, long previousVertexId)
			throws RemoteException;

	/*
	 * ======================================================== Methods to
	 * access edges ========================================================
	 */

	public int getEdgeTypeId(int localEdgeId) throws RemoteException;

	public long getNextEdgeId(int localEdgeId) throws RemoteException;

	public void setNextEdgeId(int localEdgeId, long nextEdgeId)
			throws RemoteException;

	public long getPreviousEdgeId(int localEdgeId) throws RemoteException;

	public void setPreviousEdgeId(int localEdgeId, long previousEdgeId)
			throws RemoteException;

	/*
	 * ======================================================== Methods to
	 * access incidences
	 * ========================================================
	 */

	public long getFirstIncidenceIdAtVertexId(int localElementId)
			throws RemoteException;

	public void setFirstIncidenceIdAtVertexId(int localElementId, long id)
			throws RemoteException;

	public long getLastIncidenceIdAtVertexId(int localElementId)
			throws RemoteException;

	public void setLastIncidenceIdAtVertexId(int localElementId, long id)
			throws RemoteException;

	public long getNextIncidenceIdAtVertexId(int convertToLocalId)
			throws RemoteException;

	public long getPreviousIncidenceIdAtVertexId(int convertToLocalId)
			throws RemoteException;

	public void setNextIncidenceAtVertexId(int localIncidenceId, long nextIncId)
			throws RemoteException;

	public void setPreviousIncidenceAtVertexId(int localIncidenceId,
			long previousIncId) throws RemoteException;

	public long getConnectedVertexId(int incidenceId) throws RemoteException;

	public long getIncidenceListVersionOfVertexId(int elementId)
			throws RemoteException;

	public void increaseIncidenceListVersionOfVertexId(int elementId)
			throws RemoteException;

	public long getFirstIncidenceIdAtEdgeId(int localElementId)
			throws RemoteException;

	public void setFirstIncidenceIdAtEdgeId(int localElementId, long l)
			throws RemoteException;

	public long getLastIncidenceIdAtEdgeId(int localElementId)
			throws RemoteException;

	public void setLastIncidenceIdAtEdgeId(int localElementId, long id)
			throws RemoteException;

	public long getNextIncidenceIdAtEdgeId(int convertToLocalId)
			throws RemoteException;

	public long getPreviousIncidenceIdAtEdgeId(int convertToLocalId)
			throws RemoteException;

	public void setNextIncidenceAtEdgeId(int localIncidenceId, long nextIncId)
			throws RemoteException;

	public void setPreviousIncidenceAtEdgeId(int localIncidenceId,
			long previousIncId) throws RemoteException;

	public long getConnectedEdgeId(int incidenceId) throws RemoteException;

	public long getIncidenceListVersionOfEdgeId(int elementId)
			throws RemoteException;

	public void increaseIncidenceListVersionOfEdgeId(int elementId)
			throws RemoteException;

	public int getIncidenceTypeId(int localIncidenceId) throws RemoteException;

}
