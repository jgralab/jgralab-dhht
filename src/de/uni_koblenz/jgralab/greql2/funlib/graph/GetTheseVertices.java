package de.uni_koblenz.jgralab.greql2.funlib.graph;

import java.util.HashSet;
import java.util.Set;

import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetTheseVertices extends Function {

	public GetTheseVertices() {
		super(
				"Returns the sequence of vertices connected to the edge of the given incidence with incidences of the same direction as the given incidence.",
				Category.GRAPH);
	}

	public Iterable<Vertex> evaluate(Incidence i) {
		Set<Vertex> result = new HashSet<Vertex>();
		Iterable<Incidence> incidences = i.getEdge().getIncidences(
				i.getDirection());
		for (Incidence inc : incidences) {
			result.add(inc.getVertex());
		}
		return result;
	}
}
