package de.uni_koblenz.jgralab;

import java.rmi.Remote;
import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.algolib.SatelliteAlgorithmRemoteAccess;
import de.uni_koblenz.jgralab.impl.ParentEntityKind;
import de.uni_koblenz.jgralab.impl.RemoteGraphDatabaseAccessWithInternalMethods;

public interface RemoteJGraLabServer extends Remote {

	/**
	 * Returns the GraphDatabase <code>graphDatabase</code> responsible for the
	 * storage of all graphs belonging to the complete graph identified by the
	 * given <code>uniqueGraphId</code>. If there is no such graph database, the
	 * server will try to load the graph with the given uid, if no such graph is
	 * known, an exception is thrown
	 * 
	 * @param uniqueGraphId
	 * @param graphDatabase
	 */
	public RemoteGraphDatabaseAccessWithInternalMethods getGraphDatabase(
			String graphUid) throws RemoteException;

	/**
	 * Creates a new GraphDatabase for the partial graph with the givenØ
	 * partialGraphId belonging to the complete graph with the given unique id
	 * stored on the host identified by the given name. The schema named
	 * <code>schemaName</code> is used as a graph schema, it is required that
	 * the instances of the schema are locally compiled an available in the
	 * classpath
	 * 
	 * @param schemaName
	 * @param uniqueGraphId
	 * @param hostnameOfCompleteGraph
	 * @param localPartialGraphId
	 * @param parent
	 * @param parentPartialGraphId
	 * @return
	 * @throws ClassNotFoundException
	 *             if the schema class can not be loaded
	 */
	public RemoteGraphDatabaseAccessWithInternalMethods createPartialGraphDatabase(
			String schemaName, String uniqueGraphId,
			String hostnameOfCompleteGraph, long parentGlobalEntityId,
			ParentEntityKind parent, int localPartialGraphId)
			throws ClassNotFoundException, RemoteException;

	public SatelliteAlgorithmRemoteAccess createSatelliteAlgorithm(
			String uniqueGraphId, int partialGraphId, de.uni_koblenz.jgralab.algolib.CentralAlgorithm parent)
			throws RemoteException;

	public de.uni_koblenz.jgralab.algolib.universalsearch.SatelliteAlgorithmRemoteAccess createUniversalSatelliteAlgorithm(
			String uniqueGraphId,
			int partialGraphId,
			de.uni_koblenz.jgralab.algolib.universalsearch.CentralAlgorithm parent) throws RemoteException;

	public Remote createSubgraphGenerator(String uniqueGraphId,
			int layers, int[] branchingFactors) throws RemoteException;

}