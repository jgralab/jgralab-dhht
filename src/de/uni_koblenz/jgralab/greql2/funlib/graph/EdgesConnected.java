package de.uni_koblenz.jgralab.greql2.funlib.graph;

import org.pcollections.PVector;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;
import de.uni_koblenz.jgralab.greql2.types.TypeCollection;

public class EdgesConnected extends Function {

	public EdgesConnected() {
		super(
				"Returns the list of edges of given vertex, optionally restricted by a type collection.",
				2, 5, 1.0, Category.GRAPH);
	}

	public PVector<Edge> evaluate(Vertex v) {
		return evaluate(v, null);
	}

	public PVector<Edge> evaluate(Vertex v, TypeCollection tc) {
		PVector<Edge> result = JGraLab.vector();
		for (Incidence i : v.getIncidences(Direction.BOTH)) {
			if ((tc == null) || tc.acceptsType(i.getEdge().getType())) {
				result = result.plus(i.getEdge());
			}
		}
		return result;
	}
}
