package de.uni_koblenz.jgralab;

import de.uni_koblenz.jgralab.schema.Schema;

public interface JGraLabServer {

	public abstract JGraLabServer getRemoteInstance(String hostname);

	public abstract Schema getSchema(String schemaName);

	public abstract Schema createSchema(String schemaString)
			throws GraphIOException;

	public void putGraph(String graphId, Graph graph);

	public Graph getGraph(String graphId);

	public Graph loadGraph(String filename, ProgressFunction pf)
			throws GraphIOException;

}