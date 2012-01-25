package de.uni_koblenz.jgralab.algolib.universalsearch;

import java.rmi.Remote;
import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Vertex;


/** Interface of the central algorithm containing the datastructures **/
public interface CentralAlgorithm extends Remote {

	/** start search from vertex <code>startVertex</code> **/
	public void run(Vertex startVertex) throws RemoteException;


	public boolean testAndProcessEdge(long edgeId, long incId)
			throws RemoteException;


	public boolean testAndProcessVertex(long vertexId, long incId)
			throws RemoteException;


	/** @return the partial graph id of the graph handles by this algorithm */
	public int getPartialGraphId() throws RemoteException;

}
