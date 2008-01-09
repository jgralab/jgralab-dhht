/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.greql2.schema;

import de.uni_koblenz.jgralab.Aggregation;
import de.uni_koblenz.jgralab.EdgeDirection;

import de.uni_koblenz.jgralab.greql2.schema.impl.IsTableHeaderOfImpl;
/**
FromVertexClass: Expression
FromRoleName : 
ToVertexClass: ComprehensionWithTableHeader
toRoleName : 
 */

public interface IsTableHeaderOf extends Aggregation, Greql2Aggregation {

	/**
	 * refers to the default implementation class of this interface
	 */
	public static final Class<IsTableHeaderOfImpl> IMPLEMENTATION_CLASS = IsTableHeaderOfImpl.class;

	/**
	 * @return the next Greql2Aggregation edge in the global edge sequence
	 */
	public Greql2Aggregation getNextGreql2AggregationInGraph();

	/**
	 * @return the next IsTableHeaderOf edge in the global edge sequence
	 */
	public IsTableHeaderOf getNextIsTableHeaderOfInGraph();

	/**
	 * @return the next IsTableHeaderOf edge in the global edge sequence
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsTableHeaderOf are accepted
	 */
	public IsTableHeaderOf getNextIsTableHeaderOfInGraph(boolean noSubClasses);

	/**
	 * @return the next edge of class Greql2Aggregation at the "this" vertex
	 */
	public Greql2Aggregation getNextGreql2Aggregation();

	/**
	 * @return the next edge of class Greql2Aggregation at the "this" vertex
	 * @param orientation the orientation of the edge
	 */
	public Greql2Aggregation getNextGreql2Aggregation(EdgeDirection orientation);

	/**
	 * @return the next edge of class IsTableHeaderOf at the "this" vertex
	 */
	public IsTableHeaderOf getNextIsTableHeaderOf();

	/**
	 * @return the next edge of class IsTableHeaderOf at the "this" vertex
	 * @param orientation the orientation of the edge
	 */
	public IsTableHeaderOf getNextIsTableHeaderOf(EdgeDirection orientation);

	/**
	 * @return the next edge of class IsTableHeaderOf at the "this" vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsTableHeaderOf are accepted
	 */
	public IsTableHeaderOf getNextIsTableHeaderOf(boolean noSubClasses);

	/**
	 * @return the next edge of class IsTableHeaderOf at the "this" vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsTableHeaderOf are accepted
	 */
	public IsTableHeaderOf getNextIsTableHeaderOf(EdgeDirection orientation, boolean noSubClasses);

}
