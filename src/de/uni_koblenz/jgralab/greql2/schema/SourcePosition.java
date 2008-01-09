/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.greql2.schema;

import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;

import java.io.IOException;

public class SourcePosition {

	public int length;

	public int offset;

	public SourcePosition(int length, int offset) {
		this.length = length;
		this.offset = offset;
	}

	@SuppressWarnings("unchecked")
	public SourcePosition(java.util.Map<String, Object> fields) {
		this.length = (Integer)fields.get("length");
		this.offset = (Integer)fields.get("offset");
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append("length");
		sb.append(" = ");
		sb.append(length);
		sb.append(", ");
		sb.append("offset");
		sb.append(" = ");
		sb.append(offset);
		sb.append("]");
		return sb.toString();
	}

	public SourcePosition(GraphIO io) throws GraphIOException {
		io.match("(");
		length = io.matchInteger();
		offset = io.matchInteger();
		io.match(")");
	}

	public void writeComponentValues(GraphIO io) throws IOException, GraphIOException {
		io.writeSpace();
		io.write("(");
		io.noSpace();
		io.writeInteger(length);
		io.writeInteger(offset);
		io.write(")");
	}

}
