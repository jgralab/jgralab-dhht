/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.greql2.schema;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.EdgeVertexPair;
import de.uni_koblenz.jgralab.Vertex;

import de.uni_koblenz.jgralab.greql2.schema.impl.EdgeRestrictionImpl;

public interface EdgeRestriction extends Greql2Vertex, Vertex {

	/**
	 * refers to the default implementation class of this interface
	 */
	public static final Class<EdgeRestrictionImpl> IMPLEMENTATION_CLASS = EdgeRestrictionImpl.class;

	/**
	 * @return the next EdgeRestriction vertex in the global vertex sequence
	 */
	public EdgeRestriction getNextEdgeRestriction();

	/**
	 * @return the next EdgeRestriction vertex in the global vertex sequence
	 * @param noSubClasses if set to <code>true</code>, no subclasses of EdgeRestriction are accepted
	 */
	public EdgeRestriction getNextEdgeRestriction(boolean noSubClasses);

	/**
	 * @return the next Greql2Vertex vertex in the global vertex sequence
	 */
	public Greql2Vertex getNextGreql2Vertex();

	/**
	 * @return the first edge of class IsRoleIdOf at this vertex
	 */
	public IsRoleIdOf getFirstIsRoleIdOf();

	/**
	 * @return the first edge of class IsRoleIdOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsRoleIdOf getFirstIsRoleIdOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsRoleIdOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsRoleIdOf are accepted
	 */
	public IsRoleIdOf getFirstIsRoleIdOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsRoleIdOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsRoleIdOf are accepted
	 */
	public IsRoleIdOf getFirstIsRoleIdOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsBoundExprOf at this vertex
	 */
	public IsBoundExprOf getFirstIsBoundExprOf();

	/**
	 * @return the first edge of class IsBoundExprOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsBoundExprOf getFirstIsBoundExprOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class Greql2Aggregation at this vertex
	 */
	public Greql2Aggregation getFirstGreql2Aggregation();

	/**
	 * @return the first edge of class Greql2Aggregation at this vertex
	 * @param orientation the orientation of the edge
	 */
	public Greql2Aggregation getFirstGreql2Aggregation(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsTypeExprOf at this vertex
	 */
	public IsTypeExprOf getFirstIsTypeExprOf();

	/**
	 * @return the first edge of class IsTypeExprOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsTypeExprOf getFirstIsTypeExprOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsEdgeRestrOf at this vertex
	 */
	public IsEdgeRestrOf getFirstIsEdgeRestrOf();

	/**
	 * @return the first edge of class IsEdgeRestrOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsEdgeRestrOf getFirstIsEdgeRestrOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsEdgeRestrOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsEdgeRestrOf are accepted
	 */
	public IsEdgeRestrOf getFirstIsEdgeRestrOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsEdgeRestrOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsEdgeRestrOf are accepted
	 */
	public IsEdgeRestrOf getFirstIsEdgeRestrOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsTypeIdOf at this vertex
	 */
	public IsTypeIdOf getFirstIsTypeIdOf();

	/**
	 * @return the first edge of class IsTypeIdOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsTypeIdOf getFirstIsTypeIdOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsTypeIdOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsTypeIdOf are accepted
	 */
	public IsTypeIdOf getFirstIsTypeIdOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsTypeIdOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsTypeIdOf are accepted
	 */
	public IsTypeIdOf getFirstIsTypeIdOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type IsRoleIdOf or subtypes
	 */
	public Iterable<EdgeVertexPair<? extends IsRoleIdOf, ? extends Vertex>> getIsRoleIdOfIncidences();
	
	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type IsRoleIdOf
	 * @param noSubClasses toggles wether subclasses of IsRoleIdOf should be excluded
	 */
	public Iterable<EdgeVertexPair<? extends IsRoleIdOf, ? extends Vertex>> getIsRoleIdOfIncidences(boolean noSubClasses);
	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type IsRoleIdOf
	 * @param direction EdgeDirection.IN or EdgeDirection.OUT, only edges of this direction will be included in the iterable
	 * @param noSubClasses toggles wether subclasses of IsRoleIdOf should be excluded
	 */
	public Iterable<EdgeVertexPair<? extends IsRoleIdOf, ? extends Vertex>> getIsRoleIdOfIncidences(EdgeDirection direction, boolean noSubClasses);
	
	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type IsRoleIdOf
	 * @param direction EdgeDirection.IN or EdgeDirection.OUT, only edges of this direction will be included in the iterable
	 */
	public Iterable<EdgeVertexPair<? extends IsRoleIdOf, ? extends Vertex>> getIsRoleIdOfIncidences(EdgeDirection direction);

	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type IsEdgeRestrOf or subtypes
	 */
	public Iterable<EdgeVertexPair<? extends IsEdgeRestrOf, ? extends Vertex>> getIsEdgeRestrOfIncidences();
	
	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type IsEdgeRestrOf
	 * @param noSubClasses toggles wether subclasses of IsEdgeRestrOf should be excluded
	 */
	public Iterable<EdgeVertexPair<? extends IsEdgeRestrOf, ? extends Vertex>> getIsEdgeRestrOfIncidences(boolean noSubClasses);
	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type IsEdgeRestrOf
	 * @param direction EdgeDirection.IN or EdgeDirection.OUT, only edges of this direction will be included in the iterable
	 * @param noSubClasses toggles wether subclasses of IsEdgeRestrOf should be excluded
	 */
	public Iterable<EdgeVertexPair<? extends IsEdgeRestrOf, ? extends Vertex>> getIsEdgeRestrOfIncidences(EdgeDirection direction, boolean noSubClasses);
	
	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type IsEdgeRestrOf
	 * @param direction EdgeDirection.IN or EdgeDirection.OUT, only edges of this direction will be included in the iterable
	 */
	public Iterable<EdgeVertexPair<? extends IsEdgeRestrOf, ? extends Vertex>> getIsEdgeRestrOfIncidences(EdgeDirection direction);

	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type IsTypeIdOf or subtypes
	 */
	public Iterable<EdgeVertexPair<? extends IsTypeIdOf, ? extends Vertex>> getIsTypeIdOfIncidences();
	
	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type IsTypeIdOf
	 * @param noSubClasses toggles wether subclasses of IsTypeIdOf should be excluded
	 */
	public Iterable<EdgeVertexPair<? extends IsTypeIdOf, ? extends Vertex>> getIsTypeIdOfIncidences(boolean noSubClasses);
	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type IsTypeIdOf
	 * @param direction EdgeDirection.IN or EdgeDirection.OUT, only edges of this direction will be included in the iterable
	 * @param noSubClasses toggles wether subclasses of IsTypeIdOf should be excluded
	 */
	public Iterable<EdgeVertexPair<? extends IsTypeIdOf, ? extends Vertex>> getIsTypeIdOfIncidences(EdgeDirection direction, boolean noSubClasses);
	
	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type IsTypeIdOf
	 * @param direction EdgeDirection.IN or EdgeDirection.OUT, only edges of this direction will be included in the iterable
	 */
	public Iterable<EdgeVertexPair<? extends IsTypeIdOf, ? extends Vertex>> getIsTypeIdOfIncidences(EdgeDirection direction);

}
