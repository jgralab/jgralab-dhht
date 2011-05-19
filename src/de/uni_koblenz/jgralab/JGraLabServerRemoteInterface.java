package de.uni_koblenz.jgralab;

import java.rmi.Remote;

import de.uni_koblenz.jgralab.impl.disk.GraphDatabase;
import de.uni_koblenz.jgralab.impl.disk.RemoteGraphDatabaseAccess;

public interface JGraLabServerRemoteInterface extends Remote {
	
	/**
	 * Returns the GraphDatabase <code>graphDatabase</code> responsible for the 
	 * storage of all graphs belonging to the complete graph identified by the 
	 * given <code>uniqueGraphId</code> 
	 * @param uniqueGraphId 
	 * @param graphDatabase
	 */
	public RemoteGraphDatabaseAccess getGraphDatabase(String graphUid);


	
	

	/**
	 * Loads the graph database identified 
	 * @param uid
	 * @return
	 * @throws GraphIOException
	 */
	//public GraphDatabase loadGraph(String uid) throws GraphIOException;
	
		
	
}