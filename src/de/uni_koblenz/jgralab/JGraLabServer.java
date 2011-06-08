package de.uni_koblenz.jgralab;

import de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl;


public interface JGraLabServer extends RemoteJGraLabServer {
	
	/**
	 * Registers the GraphDatabase <code>graphDatabase</code> as local 
	 * database responsible for the storage of all graphs belonging 
	 * to the complete graph identified by their common uniqueGraphId
	 * @param graphDatabase the graph database to be registered
	 */
	public void registerLocalGraphDatabase(GraphDatabaseBaseImpl graphDatabase);
	
	
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


	/**
	 * 
	 * @return the hostname of this server which has been set before by setHostname(...)
	 */
	public String getHostname();

	/**
	 * Sets the hostname on which this server listens to <code>host</code>
	 */
	public void setHostname(String host);
	

	
}
