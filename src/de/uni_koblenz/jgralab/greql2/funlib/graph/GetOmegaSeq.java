package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetOmegaSeq extends Function {

	public GetOmegaSeq() {
		super(
				"Returns the sequence of end-vertices if edge or outgoing edges if vertex.",
				Category.GRAPH);
	}

	public Iterable<? extends GraphElement<?, ?, ?, ?>> evaluate(
			GraphElement<?, ?, ?, ?> e) {
		if (e instanceof Vertex) {
			return ((Vertex) e).getOmegaEdges();
		} else {
			return ((Edge) e).getOmegaVertices();
		}
	}
}
