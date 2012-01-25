package de.uni_koblenz.jgralab.algolib;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SatelliteAlgorithmRemoteAccess extends Remote {

	/**
	 * process vertex given by its id <code>vertexId</code> and belonging to the
	 * local partial graph.
	 * 
	 * @implements $\mathbf{Communication~ point~ 1}$ and
	 *             $\mathbf{Communication~ point~ 3}$
	 */
	public void processVertex(long vertexId) throws RemoteException;

	/**
	 * process the root vertex identified by its id <code>vertexId</code> and
	 * belonging to the local partial graph.
	 * 
	 * @param vertexId
	 * @throws RemoteException
	 */
	public void processRoot(long vertexId) throws RemoteException;

}