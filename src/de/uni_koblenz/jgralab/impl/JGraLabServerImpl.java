package de.uni_koblenz.jgralab.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.JGraLabServer;
import de.uni_koblenz.jgralab.RemoteJGraLabServer;
import de.uni_koblenz.jgralab.algolib.CentralAlgorithm;
import de.uni_koblenz.jgralab.algolib.SatelliteAlgorithm;
import de.uni_koblenz.jgralab.algolib.SatelliteAlgorithmImpl;
import de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl;
import de.uni_koblenz.jgralab.impl.disk.GraphDatabaseElementaryMethods;
import de.uni_koblenz.jgralab.impl.disk.ParentEntityKind;
import de.uni_koblenz.jgralab.impl.disk.PartialGraphDatabase;
import de.uni_koblenz.jgralab.impl.disk.RemoteGraphDatabaseAccessWithInternalMethods;
import de.uni_koblenz.jgralab.schema.Schema;

public class JGraLabServerImpl implements RemoteJGraLabServer, JGraLabServer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6666943675150922207L;

	private static final String JGRALAB_SERVER_IDENTIFIER = "JGraLabServer";

	private static JGraLabServerImpl localInstance = null;

	private static RemoteJGraLabServer remoteAccessToLocalInstance = null;

	private static String localHostname = "141.26.70.230";

	private static String localPort = "1099";

	private final Map<String, RemoteGraphDatabaseAccessWithInternalMethods> localGraphDatabases = new HashMap<String, RemoteGraphDatabaseAccessWithInternalMethods>();

	private final Map<String, String> localFilesContainingGraphs = new HashMap<String, String>();

	private JGraLabServerImpl() {

	}

	public static JGraLabServerImpl getLocalInstance() {
		try {
			if (localInstance == null) {
				System.out.println("Creating local server");
				localInstance = new JGraLabServerImpl();
				remoteAccessToLocalInstance = (RemoteJGraLabServer) UnicastRemoteObject
						.exportObject(localInstance, 0);
				Registry registry = LocateRegistry.createRegistry(1099);
				registry.bind(JGRALAB_SERVER_IDENTIFIER,
						remoteAccessToLocalInstance);
				// RemoteJGraLabServer remote =
				// localInstance.getRemoteInstance(localInstance.localHostname);
			}
		} catch (Exception e) {
			System.out.println("Local Server: " + localInstance);
			e.printStackTrace();
		}
		return (JGraLabServerImpl) localInstance;
	}

	@Override
	public RemoteJGraLabServer getRemoteInstance(String hostname) {
		try {
			System.out.println("Try to connect to host " + hostname);
			RemoteJGraLabServer server = (RemoteJGraLabServer) Naming
					.lookup("rmi://" + hostname + "/"
							+ JGRALAB_SERVER_IDENTIFIER);
			return server;
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error in URL", e);
		} catch (RemoteException e) {
			throw new RuntimeException("Error in RemoteCommunicatio", e);
		} catch (NotBoundException e) {
			throw new RuntimeException("Error in service name", e);
		}
	}

	public RemoteGraphDatabaseAccessWithInternalMethods loadGraph(String uid)
			throws GraphIOException {
		RemoteGraphDatabaseAccessWithInternalMethods db = localGraphDatabases
				.get(uid);
		if (db == null) {
			// Depending on the data stored in the GraphIO file, either a
			// complete or a partial graph database will be created
			String filename = localFilesContainingGraphs.get(uid);
			Graph graph = GraphIO.loadGraphFromFile(filename, null,
					ImplementationType.DISK);
			db = graph.getGraphDatabase();
			localGraphDatabases.put(uid, db);
		}
		return db;
	}

	@Override
	public void registerLocalGraphDatabase(GraphDatabaseBaseImpl localDb) {
		if (!localGraphDatabases.containsKey(localDb.getUniqueGraphId())) {
			localGraphDatabases.put(localDb.getUniqueGraphId(), localDb);
		}
	}

	@Override
	public void registerFileForUid(String uid, String fileName) {
		localFilesContainingGraphs.put(uid, fileName);
	}

	@Override
	public RemoteGraphDatabaseAccessWithInternalMethods createPartialGraphDatabase(
			String schemaName, String uniqueGraphId,
			String hostnameOfCompleteGraph, long parentGlobalEntityId,
			ParentEntityKind parent, int localPartialGraphId)
			throws ClassNotFoundException {

		Class<?> schemaClass = Class.forName(schemaName);
		Schema schema = null;
		@SuppressWarnings("rawtypes")
		Class[] formalParams = {};
		Object[] actualParams = {};
		Method instanceMethod = null;
		try {
			instanceMethod = schemaClass.getMethod("instance", formalParams);
		} catch (SecurityException e) {
			throw new ClassNotFoundException("Class for schema " + schemaName
					+ " does not provide a static instance() method", e);
		} catch (NoSuchMethodException e) {
			throw new ClassNotFoundException("Class for schema " + schemaName
					+ " does not provide a static instance() method", e);
		}
		try {
			schema = (Schema) instanceMethod.invoke(null, actualParams);
		} catch (IllegalArgumentException e) {
			throw new ClassNotFoundException(
					"Static instance method of class for schema " + schemaName
							+ " can not be invoked", e);
		} catch (IllegalAccessException e) {
			throw new ClassNotFoundException(
					"Static instance method of class for schema " + schemaName
							+ " can not be invoked", e);
		} catch (InvocationTargetException e) {
			throw new ClassNotFoundException(
					"Static instance method of class for schema " + schemaName
							+ " can not be invoked", e);
		}
		GraphDatabaseBaseImpl db = new PartialGraphDatabase(schema,
				uniqueGraphId, hostnameOfCompleteGraph, parentGlobalEntityId,
				parent, localPartialGraphId);
		localGraphDatabases.put(uniqueGraphId, db);
		try {
			return (RemoteGraphDatabaseAccessWithInternalMethods) UnicastRemoteObject
					.exportObject(db, 0);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public RemoteGraphDatabaseAccessWithInternalMethods getGraphDatabase(
			String uid) {
		if (!localGraphDatabases.containsKey(uid)) {
			try {
				loadGraph(uid);
			} catch (GraphIOException e) {
				throw new GraphException(e);
			}
		}
		try {
			System.out.println("Try to export graph database");
			return (RemoteGraphDatabaseAccessWithInternalMethods) UnicastRemoteObject
					.exportObject(localGraphDatabases.get(uid), 0);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the graph database storing all data of all subgraphs belonging to
	 * the graph identified by uid
	 * 
	 * @param uid
	 * @return
	 */
	public GraphDatabaseBaseImpl getLocalGraphDatabase(String uid) {
		return (GraphDatabaseBaseImpl) localGraphDatabases.get(uid);
	}

	@Override
	public String getHostname() {
		return localHostname;
	}

	@Override
	public void setHostname(String host) {
		this.localHostname = host;
	}

	public SatelliteAlgorithm createSatelliteAlgorithm(String uniqueGraphId,
			int partialGraphId, CentralAlgorithm parent) throws RemoteException {
		Graph g = ((GraphDatabaseBaseImpl) getLocalGraphDatabase(uniqueGraphId))
				.getGraphObject(GraphDatabaseElementaryMethods
						.getToplevelGraphForPartialGraphId(partialGraphId));
		return SatelliteAlgorithmImpl.create(g, parent);
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			localHostname = args[0];
			if (args.length > 1) {
				localPort = args[1];
			}
		}
		JGraLabServer server = JGraLabServerImpl.getLocalInstance();
	}

}
