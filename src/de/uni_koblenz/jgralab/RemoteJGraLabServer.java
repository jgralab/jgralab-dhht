package de.uni_koblenz.jgralab;

import java.rmi.Remote;

import de.uni_koblenz.jgralab.impl.disk.RemoteGraphDatabaseAccess;

public interface RemoteJGraLabServer extends Remote {
	
	/**
	 * Returns the GraphDatabase <code>graphDatabase</code> responsible for the 
	 * storage of all graphs belonging to the complete graph identified by the 
	 * given <code>uniqueGraphId</code>. If there is no such graph database,
	 * a new one will be created 
	 * @param uniqueGraphId 
	 * @param graphDatabase
	 */
	public RemoteGraphDatabaseAccess getGraphDatabase(String graphUid);

	
	
}