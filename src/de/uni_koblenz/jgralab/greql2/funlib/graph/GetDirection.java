package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetDirection extends Function {

	public GetDirection() {
		super("Returns the direction of the given incidence.", Category.GRAPH);
	}

	public Direction evaluate(Incidence i) {
		return i.getDirection();
	}
}
