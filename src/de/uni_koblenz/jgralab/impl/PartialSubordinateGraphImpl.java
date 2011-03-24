package de.uni_koblenz.jgralab.impl;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.schema.GraphClass;


public abstract class PartialSubordinateGraphImpl extends PartialGraphImpl {

	private GraphElement containingElement;
	
	protected PartialSubordinateGraphImpl(String id, GraphClass cls, GraphElement elem) {
		super(id, cls, elem.getGraph());
		containingElement = elem;
	}
	
	

}
