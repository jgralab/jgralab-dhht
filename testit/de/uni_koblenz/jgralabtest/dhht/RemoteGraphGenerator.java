package de.uni_koblenz.jgralabtest.dhht;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteGraphGenerator extends Remote {

	
	public long createSubgraph() throws RemoteException;
	
}
