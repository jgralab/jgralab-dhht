package de.uni_koblenz.jgralab.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.JGraLabServer;
import de.uni_koblenz.jgralab.RemoteJGraLabServer;
import de.uni_koblenz.jgralab.schema.Schema;

public class JGraLabServerImpl implements RemoteJGraLabServer, JGraLabServer {


	private static final String JGRALAB_SERVER_IDENTIFIER = "JGraLabServer";

	private static JGraLabServerImpl localInstance = null;

	private static RemoteJGraLabServer remoteAccessToLocalInstance = null;

	private static String localHostname = "141.26.70.230";

	private final Map<String, RemoteGraphDatabaseAccessWithInternalMethods> localGraphDatabases = new HashMap<String, RemoteGraphDatabaseAccessWithInternalMethods>();

	private final Map<String, RemoteGraphDatabaseAccessWithInternalMethods> localStubs = new HashMap<String, RemoteGraphDatabaseAccessWithInternalMethods>();

	private final Map<String, String> localFilesContainingGraphs = new HashMap<String, String>();

	private JGraLabServerImpl() {

	}

	public static JGraLabServerImpl getLocalInstance() {
		try {
			if (localInstance == null) {
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
			e.printStackTrace();
		}
		return (JGraLabServerImpl) localInstance;
	}

	@Override
	public RemoteJGraLabServer getRemoteInstance(String hostname) {
		try {
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

	public RemoteGraphDatabaseAccess loadGraph(String uid, ImplementationType implType)
			throws GraphIOException {
		RemoteGraphDatabaseAccess db = localGraphDatabases
				.get(uid);
		if (db == null) {
			// Depending on the data stored in the GraphIO file, either a
			// complete or a partial graph database will be created
			String filename = localFilesContainingGraphs.get(uid);
			Graph graph = GraphIO.loadGraphFromFile(filename, null,
					ImplementationType.DISK);
			db = graph.getGraphDatabase();
			switch (implType) {
			case DISK: 
				registerLocalDiskGraphDatabase((de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl) db);
				break;
			case DISTRIBUTED: 
				registerLocalDistributedGraphDatabase((de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl) db);
				break;
			default:
				throw new RuntimeException("Unhandled case block");
			}
			

		}
		return db;
	}

	@Override
	public void registerLocalDiskGraphDatabase(de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl localDb) {
		String uniqueId = localDb.getUniqueGraphId();
		if (!localGraphDatabases.containsKey(uniqueId)) {
			localGraphDatabases.put(uniqueId, localDb);
			RemoteGraphDatabaseAccessWithInternalMethods stub;
			try {
				stub = (RemoteGraphDatabaseAccessWithInternalMethods) UnicastRemoteObject
						.exportObject(localGraphDatabases.get(uniqueId), 0);
				localStubs.put(uniqueId, stub);
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			}

		}
	}
	
	@Override
	public void registerLocalDistributedGraphDatabase(de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl localDb) {
		String uniqueId = localDb.getUniqueGraphId();
		if (!localGraphDatabases.containsKey(uniqueId)) {
			localGraphDatabases.put(uniqueId, localDb);
			RemoteGraphDatabaseAccessWithInternalMethods stub;
			try {
				stub = (RemoteGraphDatabaseAccessWithInternalMethods) UnicastRemoteObject
						.exportObject(localGraphDatabases.get(uniqueId), 0);
				localStubs.put(uniqueId, stub);
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			}

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
			ParentEntityKind parent, int localPartialGraphId, ImplementationType implType)
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
		switch (implType) {
		case DISK:
			de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl db = new de.uni_koblenz.jgralab.impl.disk.PartialGraphDatabase(schema,
				uniqueGraphId, hostnameOfCompleteGraph, parentGlobalEntityId,
				parent, localPartialGraphId);
			registerLocalDiskGraphDatabase(db);
			break;
		case DISTRIBUTED:
			de.uni_koblenz.jgralab.impl.distributed.GraphDatabaseBaseImpl db2 = new de.uni_koblenz.jgralab.impl.distributed.PartialGraphDatabase(schema,
				uniqueGraphId, hostnameOfCompleteGraph, parentGlobalEntityId,
				parent, localPartialGraphId);
			registerLocalDistributedGraphDatabase(db2);
			break;
		default:
			throw new RuntimeException("Unhandled case");
		}


		return getGraphDatabase(uniqueGraphId, implType);
	}

	@Override
	public RemoteGraphDatabaseAccessWithInternalMethods getGraphDatabase(String uid, ImplementationType implType) {
		if (!localGraphDatabases.containsKey(uid)) {
			try {
				loadGraph(uid, implType);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return localStubs.get(uid);
	}

	/**
	 * Returns the graph database storing all data of all subgraphs belonging to
	 * the graph identified by uid
	 * 
	 * @param uid
	 * @return
	 */
	public RemoteGraphDatabaseAccess getLocalGraphDatabase(String uid, ImplementationType implType) {
		if (!localGraphDatabases.containsKey(uid)) {
			try {
				loadGraph(uid, implType);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return localGraphDatabases.get(uid);
	}

	@Override
	public String getHostname() {
		return localHostname;
	}

	@Override
	public void setHostname(String host) {
		localHostname = host;
	}

	public de.uni_koblenz.jgralab.algolib.SatelliteAlgorithmRemoteAccess createSatelliteAlgorithm(
			String uniqueGraphId, int partialGraphId, de.uni_koblenz.jgralab.algolib.CentralAlgorithm parent)
			throws RemoteException {
		Graph g = ((de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl) getLocalGraphDatabase(uniqueGraphId, ImplementationType.DISK))
				.getGraphObject(de.uni_koblenz.jgralab.impl.disk.GraphDatabaseElementaryMethods
						.getToplevelGraphForPartialGraphId(partialGraphId));
		return (de.uni_koblenz.jgralab.algolib.SatelliteAlgorithmRemoteAccess) UnicastRemoteObject
				.exportObject(de.uni_koblenz.jgralab.algolib.SatelliteAlgorithmImpl.create(g, parent), 0);
	}
	
	public de.uni_koblenz.jgralab.algolib.universalsearch.SatelliteAlgorithmRemoteAccess createUniversalSatelliteAlgorithm(
			String uniqueGraphId, int partialGraphId, de.uni_koblenz.jgralab.algolib.universalsearch.CentralAlgorithm parent)
			throws RemoteException {
		Graph g = ((de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl) getLocalGraphDatabase(uniqueGraphId, ImplementationType.DISK))
				.getGraphObject(de.uni_koblenz.jgralab.impl.disk.GraphDatabaseElementaryMethods
						.getToplevelGraphForPartialGraphId(partialGraphId));
		return (de.uni_koblenz.jgralab.algolib.universalsearch.SatelliteAlgorithmRemoteAccess) UnicastRemoteObject
				.exportObject(de.uni_koblenz.jgralab.algolib.universalsearch.SatelliteAlgorithmImpl.create(g, parent), 0);
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			localHostname = args[0];
		}
		JGraLabServerImpl.getLocalInstance();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Remote createSubgraphGenerator(String uniqueGraphId,
			int layers, int[] branchingFactors) throws RemoteException {
		try {
			Class c = Class.forName("de.uni_koblenz.jgralabtest.dhht.SubgraphGenerator");
			@SuppressWarnings("unchecked")
			Constructor m = c.getConstructor(String.class, int.class, int[].class);
			Object o =  m.newInstance(uniqueGraphId, layers, branchingFactors);
		return UnicastRemoteObject
				.exportObject((Remote) o, 0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}



}
