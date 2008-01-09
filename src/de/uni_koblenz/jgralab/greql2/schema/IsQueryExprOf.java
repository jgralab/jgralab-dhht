/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.greql2.schema;

import de.uni_koblenz.jgralab.Aggregation;
import de.uni_koblenz.jgralab.EdgeDirection;

import de.uni_koblenz.jgralab.greql2.schema.impl.IsQueryExprOfImpl;
/**
FromVertexClass: Expression
FromRoleName : 
ToVertexClass: Greql2Expression
toRoleName : 
 */

public interface IsQueryExprOf extends Aggregation, Greql2Aggregation {

	/**
	 * refers to the default implementation class of this interface
	 */
	public static final Class<IsQueryExprOfImpl> IMPLEMENTATION_CLASS = IsQueryExprOfImpl.class;

	/**
	 * @return the next Greql2Aggregation edge in the global edge sequence
	 */
	public Greql2Aggregation getNextGreql2AggregationInGraph();

	/**
	 * @return the next IsQueryExprOf edge in the global edge sequence
	 */
	public IsQueryExprOf getNextIsQueryExprOfInGraph();

	/**
	 * @return the next IsQueryExprOf edge in the global edge sequence
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsQueryExprOf are accepted
	 */
	public IsQueryExprOf getNextIsQueryExprOfInGraph(boolean noSubClasses);

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
	 * @return the next edge of class IsQueryExprOf at the "this" vertex
	 */
	public IsQueryExprOf getNextIsQueryExprOf();

	/**
	 * @return the next edge of class IsQueryExprOf at the "this" vertex
	 * @param orientation the orientation of the edge
	 */
	public IsQueryExprOf getNextIsQueryExprOf(EdgeDirection orientation);

	/**
	 * @return the next edge of class IsQueryExprOf at the "this" vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsQueryExprOf are accepted
	 */
	public IsQueryExprOf getNextIsQueryExprOf(boolean noSubClasses);

	/**
	 * @return the next edge of class IsQueryExprOf at the "this" vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsQueryExprOf are accepted
	 */
	public IsQueryExprOf getNextIsQueryExprOf(EdgeDirection orientation, boolean noSubClasses);

}
