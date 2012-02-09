package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;

public class Next extends Function {
	public Next() {
		super(
				"Returns the next edge or vertex for a given element, optionally restricted by a type collection."
						, 2, 1,
				1.0, Category.GRAPH);
	}

	public Edge evaluate(Edge e) {
		return e.getNextEdge();
	}


	public Edge evaluate(Edge e, TypeCollection tc) {
		for (Edge n = e.getNextEdge(); n != null; n = n.getNextEdge()) {
			if (tc.acceptsType(n.getType())) {
				return n;
			}
		}
		return null;
	}

	public Vertex evaluate(Vertex v) {
		return v.getNextVertex();
	}

	public Vertex evaluate(Vertex v, TypeCollection tc) {
		for (Vertex n = v.getNextVertex(); n != null; n = n.getNextVertex()) {
			if (tc.acceptsType(n.getType())) {
				return n;
			}
		}
		return null;
	}
}
