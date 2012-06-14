package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetPartialGraphs extends Function {

	public GetPartialGraphs() {
		super("Returns a sequence of all partial graphs of the given graph.",
				Category.GRAPH);
	}

	public Iterable<? extends Graph> evaluate(Graph g) {
		return g.getPartialGraphs();
	}
}
