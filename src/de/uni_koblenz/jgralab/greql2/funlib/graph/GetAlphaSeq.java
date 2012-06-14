package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetAlphaSeq extends Function {

	public GetAlphaSeq() {
		super(
				"Returns the sequence of start-vertices if edge and incoming edges if vertex.",
				Category.GRAPH);
	}

	public Iterable<? extends GraphElement<?, ?, ?, ?>> evaluate(
			GraphElement<?, ?, ?, ?> e) {
		if (e instanceof Vertex) {
			return ((Vertex) e).getAlphaEdges();
		} else {
			return ((Edge) e).getAlphaVertices();
		}
	}

}
