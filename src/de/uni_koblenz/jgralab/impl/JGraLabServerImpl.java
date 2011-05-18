package de.uni_koblenz.jgralab.impl;

import java.io.File;
import java.io.StringBufferInputStream;
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
import de.uni_koblenz.jgralab.JGraLabServer;
import de.uni_koblenz.jgralab.codegenerator.CodeGeneratorConfiguration;
import de.uni_koblenz.jgralab.impl.disk.GraphDatabase;
import de.uni_koblenz.jgralab.impl.disk.GraphImpl;
import de.uni_koblenz.jgralab.impl.disk.RemoteGraphDatabaseAccess;
import de.uni_koblenz.jgralab.schema.Schema;

@SuppressWarnings("deprecation")
public class JGraLabServerImpl extends UnicastRemoteObject implements
		JGraLabServer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6666943675150922207L;

	private static final String JGRALAB_SERVER_IDENTIFIER = "JGraLabServer";

	private static JGraLabServerImpl localInstance = null;

	private final Map<String, GraphDatabase> graphs = new HashMap<String, GraphDatabase>();

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
	public JGraLabServer getRemoteInstance(String hostname) {
		try {
			JGraLabServer server = (JGraLabServer) Naming.lookup(hostname + "/"
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.JGraLabServer#getSchema(java.lang.String)
	 */
	@Override
	public Schema getSchema(String schemaName) {
		Class<?>[] params = {};
		try {
			Schema s = (Schema) Class.forName(schemaName)
					.getMethod("instance", params)
					.invoke(null, (Object[]) null);
			return s;
		} catch (Exception ex) {
			throw new RuntimeException("Error loading schema class for schema "
					+ schemaName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.JGraLabServer#createSchema(java.lang.String)
	 */
	@Override
	public Schema createSchema(String schemaString) throws GraphIOException {
		Schema s = GraphIO.loadSchemaFromStream(new StringBufferInputStream(
				schemaString));
		s.compile(CodeGeneratorConfiguration.MINIMAL);
		return s;
	}

	@Override
	public void registerGraph(String graphUid, int partialGraphId, GraphDatabase graph) {
		graphs.put(graphUid+ "::" + Integer.toString(partialGraphId), graph);
	}

	@Override
	public GraphDatabase getGraph(String graphUid, int partialGraphId) {
		return graphs.get(graphUid+ "::" + Integer.toString(partialGraphId));
	}

	@Override
	public GraphDatabase loadGraph(String uid) throws GraphIOException {
		GraphDatabase db = localGraphDatabases.get(uid);
		if (db == null) {
			File f = localFilesContainingGraphs.get(uid);
			if (f == null)
				throw new GraphException("There is no file registered containing a part of the graph identified by uid " + uid);
			((GraphImpl) GraphIO.loadGraphFromFileWithStandardSupport(f.getAbsolutePath(), null)).getGraphDatabase();
			localGraphDatabases.put(uid, db);
		}
		return db;
	}

	@Override
	public GraphDatabase createGraph(int graphClassId, String completeGraphUid,
			int partialGraphId, String hostnameOfCompleteGraph) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void registerLocalGraphDatabase(GraphDatabase localDb) {
		if (!localGraphDatabases.containsKey(localDb.getUid()))
			localGraphDatabases.put(localDb.getUniqueID(), localDb);
	}
	
	

	public void registerFileForUid(String uid, String fileName) {
		localFilesContainingGraphs.put(uid, new File(fileName));
	}	
	

	public void registerFileForUid(String uid, File file) {
		localFilesContainingGraphs.put(uid, file);
	}

	@Override
	public RemoteGraphDatabaseAccess getGraphDatabase(String uid) {
		if (!localGraphDatabases.containsKey(uid))
			loadGraph(uid);
		return localGraphDatabases.get(uid);
	}
	
	/**
	 * Returns the graph database storing all data of all subgraphs belonging to the graph 
	 * identified by uid
	 * @param uid
	 * @return
	 */
	public GraphDatabase getLocalGraphDatabase(String uid) {
		return localGraphDatabases.get(uid);
	}
	
	Map<String, GraphDatabase> localGraphDatabases = new HashMap<String, GraphDatabase>();
	
	Map<String, File> localFilesContainingGraphs = new HashMap<String, File>();
	
	
	//load partial graph of local graph
	
	JGraLabServer remote = JGraLabServerImpl.getRemoteInstance(remoteURL);
	GraphDatabase remoteDb = remote.getGraphDatabase(uid);

 	
}
