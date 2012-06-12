package de.uni_koblenz.jgralab.greql2.funlib.graph;

import java.util.HashSet;
import java.util.Set;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetTheseEdges extends Function {

	public GetTheseEdges() {
		super(
				"Returns a sequence of the edges which are connected to the vertex of the given incidence with incidences of the same direction.",
				Category.GRAPH);
	}

	public Iterable<Edge> evaluate(Incidence i) {
		Set<Edge> result = new HashSet<Edge>();
		Iterable<Incidence> incidences = i.getVertex().getIncidences(
				i.getDirection());
		for (Incidence inc : incidences) {
			result.add(inc.getEdge());
		}
		return result;
	}
}
