package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.funlib.NeedsGraphArgument;

@NeedsGraphArgument
public class GetVertex extends Function {

	public GetVertex() {
		super(
				"Returns the vertex with the given id or the vertex of the given incidence.",
				Category.GRAPH);
	}

	public Vertex evaluate(Graph graph, Integer id) {
		return graph.getVertex(id);
	}

	public Vertex evaluate(Incidence i) {
		return i.getVertex();
	}
}
