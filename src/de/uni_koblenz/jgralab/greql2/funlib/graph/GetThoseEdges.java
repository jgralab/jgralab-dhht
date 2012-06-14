package de.uni_koblenz.jgralab.greql2.funlib.graph;

import java.util.HashSet;
import java.util.Set;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetThoseEdges extends Function {

	public GetThoseEdges() {
		super(
				"Returns a sequence of edges which are connected to the vertex of the given incidence via incidences of the opposite direction.",
				Category.GRAPH);
	}

	public Iterable<Edge> evaluate(Incidence i) {
		Set<Edge> result = new HashSet<Edge>();
		Iterable<Incidence> incidences = i.getVertex().getIncidences(
				i.getDirection().getOppositeDirection());
		for (Incidence inc : incidences) {
			result.add(inc.getEdge());
		}
		return result;
	}

}
