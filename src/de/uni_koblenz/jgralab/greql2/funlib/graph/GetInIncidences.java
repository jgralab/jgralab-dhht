package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetInIncidences extends Function {

	public GetInIncidences() {
		super(
				"Returns the sequence of ingoing incidences of the given graph element.",
				Category.GRAPH);
	}

	public Iterable<Incidence> evaluate(GraphElement<?, ?, ?, ?> e) {
		return e instanceof Vertex ? e.getIncidences(Direction.EDGE_TO_VERTEX)
				: e.getIncidences(Direction.VERTEX_TO_EDGE);
	}
}
