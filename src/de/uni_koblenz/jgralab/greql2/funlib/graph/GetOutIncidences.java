package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetOutIncidences extends Function {

	public GetOutIncidences() {
		super(
				"Returns the sequence of outgoing incidences of the given graph element.",
				Category.GRAPH);
	}

	public Iterable<Incidence> evaluate(GraphElement<?, ?, ?, ?> e) {
		return e instanceof Vertex ? e.getIncidences(Direction.VERTEX_TO_EDGE)
				: e.getIncidences(Direction.EDGE_TO_VERTEX);
	}
}
