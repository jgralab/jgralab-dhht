package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetFirstIncidenceAtVertex extends Function {

	public GetFirstIncidenceAtVertex() {
		super("Returns the first incidence at the given vertex.",
				Category.GRAPH);
	}

	public Incidence evaluate(Vertex v) {
		return v.getFirstIncidence();
	}

}
