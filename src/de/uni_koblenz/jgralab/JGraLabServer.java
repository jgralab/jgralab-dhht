package de.uni_koblenz.jgralab;

import java.rmi.Remote;

import de.uni_koblenz.jgralab.impl.disk.GraphDatabase;
import de.uni_koblenz.jgralab.impl.disk.RemoteGraphDatabaseAccess;
import de.uni_koblenz.jgralab.schema.Schema;

public interface JGraLabServer extends Remote {

	public abstract JGraLabServer getRemoteInstance(String hostname);

	public abstract Schema getSchema(String schemaName);

	public abstract Schema createSchema(String schemaString)
			throws GraphIOException;

	public void registerGraph(String graphId, int partialGraphId, GraphDatabase graph);

	public GraphDatabase getGraph(String graphUid, int partialGraphId);

	public GraphDatabase loadGraph(String filename, ProgressFunction pf)
			throws GraphIOException;

	public abstract GraphDatabase createGraph(int graphClassId, String completeGraphUid, int partialGraphId, String hostnameOfCompleteGraph);

	/**
	 * Method to register a local file containing a part of the graph identified by uid
	 * @param uid
	 * @param file
	 */
	//public void registerFileForUid(String uid, File file);
	
	/**
	 * Returns the graph database storing all data of all subgraphs belonging to the graph 
	 * identified by uid
	 * @param uid
	 * @return
	 */
	public RemoteGraphDatabaseAccess getGraphDatabase(String uid);

	/**
	 * Loads the graph database identified 
	 * @param uid
	 * @return
	 * @throws GraphIOException
	 */
	//public GraphDatabase loadGraph(String uid) throws GraphIOException;
	
		
	
}