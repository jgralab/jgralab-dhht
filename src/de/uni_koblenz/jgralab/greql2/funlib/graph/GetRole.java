package de.uni_koblenz.jgralab.greql2.funlib.graph;

import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.greql2.funlib.Function;

public class GetRole extends Function {

	public GetRole() {
		super("Returns the role(name) of the given incidence.", Category.GRAPH);
	}

	public String evaluate(Incidence i) {
		return i.getType().getRolename();
	}

}
