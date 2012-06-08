package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetIncidences extends Function {

	public GetIncidences() {
		super("Returns the sequence of incidences of the given graph element.",
				Category.GRAPH);
	}

	public Iterable<Incidence> evaluate(GraphElement<?, ?, ?, ?> e) {
		return e.getIncidences();
	}
}
