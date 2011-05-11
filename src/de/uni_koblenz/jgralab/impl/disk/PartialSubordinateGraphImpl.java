package de.uni_koblenz.jgralab.impl.disk;

import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.schema.GraphClass;

/**
 * Subordinate graph which is distributed on an other station of a network and
 * thus is also a partial graph.
 * 
 * @author dbildh
 * 
 *         TODO: Implement class according to combined functionality of Partial
 *         and subordinate graph
 * 
 *         Implement creation method in GraphElement to create a subordinate
 *         graph on another host
 * 
 */
public abstract class PartialSubordinateGraphImpl extends PartialGraphImpl {

	private final GraphElement<?, ?, ?> containingElement;

	protected PartialSubordinateGraphImpl(GraphClass cls, GraphElement elem, GraphDatabase graphDatabase) {
		super(cls, graphDatabase);
		containingElement = elem;
	}

}
