package de.uni_koblenz.jgralab.greql2.funlib.graph;

import java.util.HashSet;
import java.util.Set;

import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetThoseVertices extends Function {

	public GetThoseVertices() {
		super(
				"Returns a sequence of vertices connected to the edge of the given incidence with incidences of the other direction than the given incidence.",
				Category.GRAPH);
	}

	public Iterable<Vertex> evaluate(Incidence i) {
		Set<Vertex> result = new HashSet<Vertex>();
		Iterable<Incidence> incidences = i.getEdge().getIncidences(
				i.getDirection().getOppositeDirection());
		for (Incidence inc : incidences) {
			result.add(inc.getVertex());
		}
		return result;
	}
}
