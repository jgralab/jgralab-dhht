/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.greql2.schema;

import de.uni_koblenz.jgralab.Aggregation;
import de.uni_koblenz.jgralab.EdgeDirection;

import de.uni_koblenz.jgralab.greql2.schema.impl.IsNullExprOfImpl;
/**
FromVertexClass: Expression
FromRoleName : 
ToVertexClass: ConditionalExpression
toRoleName : 
 */

public interface IsNullExprOf extends Aggregation, Greql2Aggregation {

	/**
	 * refers to the default implementation class of this interface
	 */
	public static final Class<IsNullExprOfImpl> IMPLEMENTATION_CLASS = IsNullExprOfImpl.class;

	/**
	 * @return the next Greql2Aggregation edge in the global edge sequence
	 */
	public Greql2Aggregation getNextGreql2AggregationInGraph();

	/**
	 * @return the next IsNullExprOf edge in the global edge sequence
	 */
	public IsNullExprOf getNextIsNullExprOfInGraph();

	/**
	 * @return the next IsNullExprOf edge in the global edge sequence
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsNullExprOf are accepted
	 */
	public IsNullExprOf getNextIsNullExprOfInGraph(boolean noSubClasses);

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
	 * @return the next edge of class IsNullExprOf at the "this" vertex
	 */
	public IsNullExprOf getNextIsNullExprOf();

	/**
	 * @return the next edge of class IsNullExprOf at the "this" vertex
	 * @param orientation the orientation of the edge
	 */
	public IsNullExprOf getNextIsNullExprOf(EdgeDirection orientation);

	/**
	 * @return the next edge of class IsNullExprOf at the "this" vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsNullExprOf are accepted
	 */
	public IsNullExprOf getNextIsNullExprOf(boolean noSubClasses);

	/**
	 * @return the next edge of class IsNullExprOf at the "this" vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsNullExprOf are accepted
	 */
	public IsNullExprOf getNextIsNullExprOf(EdgeDirection orientation, boolean noSubClasses);

}
