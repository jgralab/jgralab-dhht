package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetPreviousIncidenceAtVertex extends Function {

	public GetPreviousIncidenceAtVertex() {
		super(
				"Returns the previous incidence of the given incidence at its vertex.",
				Category.GRAPH);
	}

	public Incidence evaluate(Incidence i) {
		return i.getPreviousIncidenceAtVertex();
	}

}
