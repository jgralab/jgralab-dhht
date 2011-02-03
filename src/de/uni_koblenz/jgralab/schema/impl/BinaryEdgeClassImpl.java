package de.uni_koblenz.jgralab.schema.impl;

import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.schema.BinaryEdgeClass;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.GraphClass;
import de.uni_koblenz.jgralab.schema.IncidenceClass;
import de.uni_koblenz.jgralab.schema.Package;
import de.uni_koblenz.jgralab.schema.Schema;

public class BinaryEdgeClassImpl extends EdgeClassImpl implements
		BinaryEdgeClass {

	static BinaryEdgeClass createDefaultBinaryEdgeClass(Schema schema) {
		assert schema.getDefaultGraphClass() != null : "DefaultGraphClass has not yet been created!";
		assert schema.getDefaultVertexClass() != null : "DefaultVertexClass has not yet been created!";
		assert schema.getDefaultEdgeClass() != null : "DefaultEdgeClass has not yet been created!";
		assert schema.getDefaultBinaryEdgeClass() == null : "DefaultBinaryEdgeClass already created!";
		BinaryEdgeClass bec = schema.getDefaultGraphClass().createBinaryEdgeClass(DEFAULTBINARYEDGECLASS_NAME);
		// , 0,
		// Integer.MAX_VALUE, "", AggregationKind.NONE,
		// schema.getDefaultVertexClass(), 0, Integer.MAX_VALUE, "",
		// AggregationKind.NONE);
		bec.setAbstract(true);
		return bec;
	}
	
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
	
	@Override
	public EdgeClass getDefaultClass() {
		return graphClass.getSchema().getDefaultBinaryEdgeClass();
	}

}
