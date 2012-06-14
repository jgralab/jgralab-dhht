package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetParentGraph extends Function {

	public GetParentGraph() {
		super("Returns the parent graph of the given element.", Category.GRAPH);
	}

	public Graph evaluate(Graph g) {
		return g.getParentGraph();
	}

	public Graph evaluate(GraphElement<?, ?, ?, ?> e) {
		return e.getContainingGraph();
	}
}
