package de.uni_koblenz.jgralab;

import de.uni_koblenz.jgralab.impl.disk.GraphDatabase;


public interface JGraLabServer extends RemoteJGraLabServer {
	
	/**
	 * Registers the GraphDatabase <code>graphDatabase</code> as local 
	 * database responsible for the storage of all graphs belonging 
	 * to the complete graph identified by their common uniqueGraphId
	 * @param graphDatabase the graph database to be registered
	 */
	public void registerLocalGraphDatabase(GraphDatabase graphDatabase);
	
	
	/**
	 * Retrieves the remote Server instance running on the given host
	 * @param hostname the name of the remote host
	 * @return
	 */
	public RemoteJGraLabServer getRemoteInstance(String hostname);
	
	
	/**
	 * Register a local file containing a part of the graph identified by uid
	 * @param uid
	 * @param file
	 */
	public void registerFileForUid(String uniqueGraphId, String fileName);



	
}
