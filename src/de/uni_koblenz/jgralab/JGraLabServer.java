package de.uni_koblenz.jgralab;

import de.uni_koblenz.jgralab.impl.disk.GraphDatabase;


public interface JGraLabServer extends JGraLabServerRemoteInterface {
	
	/**
	 * Registers the GraphDatabase <code>graphDatabase</code> as local 
	 * database responsible for the storage of all graphs belonging 
	 * to the complete graph identified by the given <code>uniqueGraphId</code> 
	 * @param uniqueGraphId 
	 * @param graphDatabase
	 */
	public void registerGraphDatabase(String graphId, GraphDatabase graphDatabase);
	
	
	/**
	 * Retrieves the remote Server instance running on the given host
	 * @param hostname the name of the remote host
	 * @return
	 */
	public JGraLabServerRemoteInterface getRemoteInstance(String hostname);
	
	
	/**
	 * Register a local file containing a part of the graph identified by uid
	 * @param uid
	 * @param file
	 */
	public void registerFileForUid(String uid, String fileName);

	

	
}
