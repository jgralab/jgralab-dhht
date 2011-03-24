package de.uni_koblenz.jgralab;

import de.uni_koblenz.jgralab.schema.Schema;

public interface JGraLabServer {

	public abstract JGraLabServer getRemoteInstance(String hostname);

	public abstract Schema getSchema(String schemaName);

	public abstract Schema createSchema(String schemaString)
			throws GraphIOException;

}