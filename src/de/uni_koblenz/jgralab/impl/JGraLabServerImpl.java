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
import de.uni_koblenz.jgralab.impl.disk.RemoteGraphDatabaseAccess;
import de.uni_koblenz.jgralab.schema.Schema;

public class JGraLabServerImpl implements RemoteJGraLabServer, JGraLabServer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6666943675150922207L;

	private static final String JGRALAB_SERVER_IDENTIFIER = "JGraLabServer";

	private static JGraLabServerImpl localInstance = null;
	
	private static RemoteJGraLabServer remoteAccessToLocalInstance = null;
	
	private String localHostname = "127.0.0.1";

	private final Map<String, GraphDatabaseBaseImpl> localGraphDatabases = new HashMap<String, GraphDatabaseBaseImpl>();

	private final Map<String, String> localFilesContainingGraphs = new HashMap<String, String>();

	
	
	
	private JGraLabServerImpl() {

	}
	


	public static JGraLabServerImpl getLocalInstance() {
		 try {
			if (localInstance == null) {
				System.out.println("Creatign local server");
				localInstance = new JGraLabServerImpl();
			    remoteAccessToLocalInstance = (RemoteJGraLabServer) UnicastRemoteObject.exportObject(localInstance, 0);
		        Registry registry = LocateRegistry.createRegistry(0);
		        registry.bind(JGRALAB_SERVER_IDENTIFIER, remoteAccessToLocalInstance);
			}
		 } catch (Exception e) {
			 System.out.println("Local Server: " + localInstance);

				// TODO Auto-generated catch block
				e.printStackTrace();
		}

		return (JGraLabServerImpl) localInstance;
	}

	@Override
	public RemoteJGraLabServer getRemoteInstance(String hostname) {
		try {
			RemoteJGraLabServer server = (RemoteJGraLabServer) Naming
					.lookup(hostname + "/" + JGRALAB_SERVER_IDENTIFIER);
			return server;
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error in URL", e);
		} catch (RemoteException e) {
			throw new RuntimeException("Error in RemoteCommunicatio", e);
		} catch (NotBoundException e) {
			throw new RuntimeException("Error in service name", e);
		}
	}

	public GraphDatabaseBaseImpl loadGraph(String uid) throws GraphIOException {
		GraphDatabaseBaseImpl db = localGraphDatabases.get(uid);
		if (db == null) {
			//Depending on the data stored in the GraphIO file, either a
			//complete or a partial graph database will be created
			String filename = localFilesContainingGraphs.get(uid);
			Graph graph = GraphIO.loadGraphFromFile(filename, null,  ImplementationType.DISK);
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
	public RemoteGraphDatabaseAccess createPartialGraphDatabase(String schemaName, String uniqueGraphId, String hostnameOfCompleteGraph, long parentGlobalEntityId, ParentEntityKind parent, int localPartialGraphId) throws ClassNotFoundException {
		
		Class<?> schemaClass = Class.forName(schemaName);
		Schema schema = null;
		@SuppressWarnings("rawtypes")
		Class[] formalParams = {};
		Object[] actualParams = {};
		Method instanceMethod = null;
		try {
			instanceMethod = schemaClass.getMethod("instance", formalParams);
		} catch (SecurityException e) {
			throw new ClassNotFoundException("Class for schema " + schemaName + " does not provide a static instance() method", e);
		} catch (NoSuchMethodException e) {
			throw new ClassNotFoundException("Class for schema " + schemaName + " does not provide a static instance() method", e);
		}
		try {
			schema = (Schema) instanceMethod.invoke(null, actualParams);
		} catch (IllegalArgumentException e) {
			throw new ClassNotFoundException("Static instance method of class for schema " + schemaName + " can not be invoked", e);
		} catch (IllegalAccessException e) {
			throw new ClassNotFoundException("Static instance method of class for schema " + schemaName + " can not be invoked", e);
		} catch (InvocationTargetException e) {
			throw new ClassNotFoundException("Static instance method of class for schema " + schemaName + " can not be invoked", e);
		}
		GraphDatabaseBaseImpl db = new PartialGraphDatabase(schema, uniqueGraphId, hostnameOfCompleteGraph, parentGlobalEntityId, parent, localPartialGraphId);
		localGraphDatabases.put(uniqueGraphId, db);
		return db;
	}
	

	@Override
	public RemoteGraphDatabaseAccess getGraphDatabase(String uid) {
		if (!localGraphDatabases.containsKey(uid)) {
			try {
				loadGraph(uid);
			} catch (GraphIOException e) {
				throw new GraphException(e);
			}
		}
		return localGraphDatabases.get(uid);
	}

	/**
	 * Returns the graph database storing all data of all subgraphs belonging to
	 * the graph identified by uid
	 * 
	 * @param uid
	 * @return
	 */
	public GraphDatabaseBaseImpl getLocalGraphDatabase(String uid) {
		return localGraphDatabases.get(uid);
	}

	@Override
	public String getHostname() {
		return localHostname;
	}

	@Override
	public void setHostname(String host) {
		this.localHostname = host;
	}

	
	public SatelliteAlgorithm createSatelliteAlgorithm(String uniqueGraphId, int partialGraphId, CentralAlgorithm parent) {
		Graph g = getLocalGraphDatabase(uniqueGraphId).getGraphObject(GraphDatabaseElementaryMethods.getToplevelGraphForPartialGraphId(partialGraphId));
		return SatelliteAlgorithmImpl.create(g, parent);
	}
	
	
	public static void main(String[] args) {

				JGraLabServer server = JGraLabServerImpl.getLocalInstance();

		
		
	}
	
}
