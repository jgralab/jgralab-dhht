package de.uni_koblenz.jgralab.algolib;

import java.rmi.Remote;
import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Vertex;

/** Interface of the central algorithm containing the datastructures **/
public interface CentralAlgorithm extends Remote {

	/** start search from vertex <code>startVertex</code> **/
	public void run(Vertex startVertex) throws RemoteException;

	/**
	 * process edge given by its id <code>edgeId</code> from incidence given by
	 * <code>incId</code> if it has not been processed before
	 * 
	 * @return true if the edge has been processed in this call, false if the
	 *         edge has been processes earlier
	 * @implements $\mathbf{Communication~ point ~2}$
	 */
	public boolean testAndProcessEdge(long edgeId, long incId)
			throws RemoteException;

	/**
	 * process vertex given by its id <code>vertexId</code> from incidence given
	 * by <code>incId</code> if it has not been processed before
	 * 
	 * @return true if the vertex has been processed in this call, false if the
	 *         vertex has been processes earlier
	 * @implements $\mathbf{Communication~ point~ 4}$
	 */
	public boolean testAndProcessVertex(long vertexId, long incId)
			throws RemoteException;

	/**
	 * handles root vertex identified by its id <code>rootId</code> on the
	 * central station
	 */
	public void handleRoot(long rootId) throws RemoteException;

	/**
	 * handles vertex identified by its id <code>vertexId</code> on the central
	 * station
	 */
	public void handleVertex(long vertexId) throws RemoteException;

	/**
	 * handles edge identified by its id <code>edgeId</code> on the central
	 * station
	 */
	public void handleEdge(long edgeId) throws RemoteException;

	/**
	 * handles tree incidence identified by its id <code>incidenceId</code> on
	 * the central station
	 */
	public void handleTreeIncidence(long incidenceId) throws RemoteException;

	/**
	 * handles cross incidence identified by its id <code>incidenceId</code> on
	 * the central station
	 */
	public void handleCrossIncidence(long incidenceId) throws RemoteException;

	/** @return the partial graph id of the graph handles by this algorithm */
	public int getPartialGraphId() throws RemoteException;

}
