package de.uni_koblenz.jgralab.schema.impl;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.schema.BinaryEdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.Package;

public class BinaryEdgeClassImpl extends EdgeClassImpl implements
		BinaryEdgeClass {

	protected BinaryEdgeClassImpl(String simpleName, Package pkg,
			GraphClass graphClass) {
		super(simpleName, pkg, graphClass);
	}

	@Override
	public boolean isBinary() {
		return true;
	}

	@Override
	public IncidenceClass getToIncidenceClass() {
		for (IncidenceClass ic : getIncidenceClasses()) {
			if (ic.getDirection() == Direction.EDGE_TO_VERTEX) {
				return ic;
			}
		}
		return null;
	}

	@Override
	public IncidenceClass getFromIncidenceClass() {
		for (IncidenceClass ic : getIncidenceClasses()) {
			if (ic.getDirection() == Direction.VERTEX_TO_EDGE) {
				return ic;
			}
		}
		return null;
	}
}
