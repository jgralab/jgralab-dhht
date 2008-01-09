/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.greql2.schema;

import de.uni_koblenz.jgralab.Aggregation;
import de.uni_koblenz.jgralab.EdgeDirection;

import de.uni_koblenz.jgralab.greql2.schema.impl.IsSequenceElementOfImpl;
/**
FromVertexClass: PathDescription
FromRoleName : 
ToVertexClass: SequentialPathDescription
toRoleName : 
 */

public interface IsSequenceElementOf extends Aggregation, Greql2Aggregation, IsPathDescriptionOf {

	/**
	 * refers to the default implementation class of this interface
	 */
	public static final Class<IsSequenceElementOfImpl> IMPLEMENTATION_CLASS = IsSequenceElementOfImpl.class;

	/**
	 * @return the next Greql2Aggregation edge in the global edge sequence
	 */
	public Greql2Aggregation getNextGreql2AggregationInGraph();

	/**
	 * @return the next IsPathDescriptionOf edge in the global edge sequence
	 */
	public IsPathDescriptionOf getNextIsPathDescriptionOfInGraph();

	/**
	 * @return the next IsSequenceElementOf edge in the global edge sequence
	 */
	public IsSequenceElementOf getNextIsSequenceElementOfInGraph();

	/**
	 * @return the next IsSequenceElementOf edge in the global edge sequence
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsSequenceElementOf are accepted
	 */
	public IsSequenceElementOf getNextIsSequenceElementOfInGraph(boolean noSubClasses);

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
	 * @return the next edge of class IsPathDescriptionOf at the "this" vertex
	 */
	public IsPathDescriptionOf getNextIsPathDescriptionOf();

	/**
	 * @return the next edge of class IsPathDescriptionOf at the "this" vertex
	 * @param orientation the orientation of the edge
	 */
	public IsPathDescriptionOf getNextIsPathDescriptionOf(EdgeDirection orientation);

	/**
	 * @return the next edge of class IsSequenceElementOf at the "this" vertex
	 */
	public IsSequenceElementOf getNextIsSequenceElementOf();

	/**
	 * @return the next edge of class IsSequenceElementOf at the "this" vertex
	 * @param orientation the orientation of the edge
	 */
	public IsSequenceElementOf getNextIsSequenceElementOf(EdgeDirection orientation);

	/**
	 * @return the next edge of class IsSequenceElementOf at the "this" vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsSequenceElementOf are accepted
	 */
	public IsSequenceElementOf getNextIsSequenceElementOf(boolean noSubClasses);

	/**
	 * @return the next edge of class IsSequenceElementOf at the "this" vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsSequenceElementOf are accepted
	 */
	public IsSequenceElementOf getNextIsSequenceElementOf(EdgeDirection orientation, boolean noSubClasses);

}
