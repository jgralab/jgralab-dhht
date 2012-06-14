package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.funlib.NeedsGraphArgument;

@NeedsGraphArgument
public class GetEdge extends Function {

	public GetEdge() {
		super(
				"Returns the edge with the given id or the edge of the given incidence.",
				Category.GRAPH);
	}

	public Edge evaluate(Graph graph, Integer id) {
		return graph.getEdge(id);
	}

	public Edge evaluate(Incidence i) {
		return i.getEdge();
	}
}
