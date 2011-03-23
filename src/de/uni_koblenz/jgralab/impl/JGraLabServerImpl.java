package de.uni_koblenz.jgralab.impl;

import java.io.StringBufferInputStream;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.JGraLabServer;
import de.uni_koblenz.jgralab.ProgressFunction;
import de.uni_koblenz.jgralab.codegenerator.CodeGeneratorConfiguration;
import de.uni_koblenz.jgralab.impl.disk.CompleteOrPartialGraphImpl;
import de.uni_koblenz.jgralab.impl.disk.GraphDatabase;
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
	public GraphDatabase loadGraph(String filename, ProgressFunction pf)
			throws GraphIOException {
		return ((CompleteOrPartialGraphImpl)GraphIO.loadSchemaAndGraphFromFile(filename,
				CodeGeneratorConfiguration.MINIMAL, pf)).getGraphDatabase();
	}

}
