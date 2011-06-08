package de.uni_koblenz.jgralab.impl;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import de.uni_koblenz.jgralab.GraphException;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.ImplementationType;
import de.uni_koblenz.jgralab.JGraLabServer;
import de.uni_koblenz.jgralab.RemoteJGraLabServer;
import de.uni_koblenz.jgralab.impl.disk.GraphDatabaseBaseImpl;
import de.uni_koblenz.jgralab.impl.disk.GraphImpl;
import de.uni_koblenz.jgralab.impl.disk.RemoteGraphDatabaseAccess;

public class JGraLabServerImpl extends UnicastRemoteObject implements
		JGraLabServer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6666943675150922207L;

	private static final String JGRALAB_SERVER_IDENTIFIER = "JGraLabServer";

	private static JGraLabServerImpl localInstance = null;
	
	private String localHostname = "127.0.0.1";

	private final Map<String, GraphDatabaseBaseImpl> localGraphDatabases = new HashMap<String, GraphDatabaseBaseImpl>();

	private final Map<String, String> localFilesContainingGraphs = new HashMap<String, String>();

	private JGraLabServerImpl() throws RemoteException, MalformedURLException,
			AlreadyBoundException {
		Naming.bind(JGRALAB_SERVER_IDENTIFIER, this);
	}

	public static JGraLabServer getLocalInstance() {
		try {
			if (localInstance == null) {
				localInstance = new JGraLabServerImpl();
			}
		} catch (Exception ex) {
			throw new RuntimeException("Error creating local server instance"
					+ ex);
		}
		return localInstance;
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
			String filename = localFilesContainingGraphs.get(uid);
			((GraphImpl) GraphIO.loadGraphFromFile(filename, null,  ImplementationType.DISK)).getGraphDatabase();
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

	public void registerFileForUid(String uid, String fileName) {
		localFilesContainingGraphs.put(uid, fileName);
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

}
