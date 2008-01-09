/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.greql2.schema;

import de.uni_koblenz.jgralab.Aggregation;
import de.uni_koblenz.jgralab.EdgeDirection;
/**
FromVertexClass: Expression
FromRoleName : 
ToVertexClass: Vertex
toRoleName : 
 */

public interface IsBoundExprOf extends Aggregation, Greql2Aggregation {

	/**
	 * @return the next Greql2Aggregation edge in the global edge sequence
	 */
	public Greql2Aggregation getNextGreql2AggregationInGraph();

	/**
	 * @return the next IsBoundExprOf edge in the global edge sequence
	 */
	public IsBoundExprOf getNextIsBoundExprOfInGraph();

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
	 * @return the next edge of class IsBoundExprOf at the "this" vertex
	 */
	public IsBoundExprOf getNextIsBoundExprOf();

	/**
	 * @return the next edge of class IsBoundExprOf at the "this" vertex
	 * @param orientation the orientation of the edge
	 */
	public IsBoundExprOf getNextIsBoundExprOf(EdgeDirection orientation);

}
