package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetIncidentElements extends Function {

	public GetIncidentElements() {
		super(
				"Returns the sequence of elements connected to the given element with one incidence.",
				Category.GRAPH);
	}

	public Iterable<? extends GraphElement<?, ?, ?, ?>> evaluate(
			GraphElement<?, ?, ?, ?> e) {
		if (e instanceof Vertex) {
			return ((Vertex) e).getIncidentEdges();
		} else {
			return ((Edge) e).getIncidentVertices();
		}
	}
}
