package de.uni_koblenz.jgralab;

import de.uni_koblenz.jgralab.impl.disk.GraphDatabase;
import de.uni_koblenz.jgralab.schema.Schema;

public interface JGraLabServer {

	public abstract JGraLabServer getRemoteInstance(String hostname);

	public abstract Schema getSchema(String schemaName);

	public abstract Schema createSchema(String schemaString)
			throws GraphIOException;

	public void registerGraph(String graphId, int partialGraphId, GraphDatabase graph);

	public GraphDatabase getGraph(String graphUid, int partialGraphId);

	public GraphDatabase loadGraph(String filename, ProgressFunction pf)
			throws GraphIOException;

	public abstract GraphDatabase createGraph(int graphClassId, String completeGraphUid, int partialGraphId, String hostnameOfCompleteGraph);


}