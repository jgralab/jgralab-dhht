package de.uni_koblenz.jgralab.algolib.universalsearch;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SatelliteAlgorithmRemoteAccess extends Remote {

	public boolean testAndEnqueueEdge(long edgeId, long incId) throws RemoteException;
	
	public boolean testAndEnqueueVertex(long edgeId, long incId) throws RemoteException;
	
	public void enqueueRoot(long rootId) throws RemoteException;
	
	public boolean isWorking()  throws RemoteException;
	
	public void stop() throws RemoteException;

}