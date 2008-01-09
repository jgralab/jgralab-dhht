/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.greql2.schema;

import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.EdgeVertexPair;
import de.uni_koblenz.jgralab.Vertex;

import de.uni_koblenz.jgralab.greql2.schema.impl.FunctionIdImpl;

public interface FunctionId extends Expression, Greql2Vertex, Identifier, Vertex {

	/**
	 * refers to the default implementation class of this interface
	 */
	public static final Class<FunctionIdImpl> IMPLEMENTATION_CLASS = FunctionIdImpl.class;

	/**
	 * @return the next Expression vertex in the global vertex sequence
	 */
	public Expression getNextExpression();

	/**
	 * @return the next FunctionId vertex in the global vertex sequence
	 */
	public FunctionId getNextFunctionId();

	/**
	 * @return the next FunctionId vertex in the global vertex sequence
	 * @param noSubClasses if set to <code>true</code>, no subclasses of FunctionId are accepted
	 */
	public FunctionId getNextFunctionId(boolean noSubClasses);

	/**
	 * @return the next Greql2Vertex vertex in the global vertex sequence
	 */
	public Greql2Vertex getNextGreql2Vertex();

	/**
	 * @return the next Identifier vertex in the global vertex sequence
	 */
	public Identifier getNextIdentifier();

	/**
	 * @return the next Identifier vertex in the global vertex sequence
	 * @param noSubClasses if set to <code>true</code>, no subclasses of Identifier are accepted
	 */
	public Identifier getNextIdentifier(boolean noSubClasses);

	/**
	 * @return the first edge of class IsConditionOf at this vertex
	 */
	public IsConditionOf getFirstIsConditionOf();

	/**
	 * @return the first edge of class IsConditionOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsConditionOf getFirstIsConditionOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsConditionOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsConditionOf are accepted
	 */
	public IsConditionOf getFirstIsConditionOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsConditionOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsConditionOf are accepted
	 */
	public IsConditionOf getFirstIsConditionOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsTypeRestrOf at this vertex
	 */
	public IsTypeRestrOf getFirstIsTypeRestrOf();

	/**
	 * @return the first edge of class IsTypeRestrOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsTypeRestrOf getFirstIsTypeRestrOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsTypeRestrOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsTypeRestrOf are accepted
	 */
	public IsTypeRestrOf getFirstIsTypeRestrOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsTypeRestrOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsTypeRestrOf are accepted
	 */
	public IsTypeRestrOf getFirstIsTypeRestrOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsConstraintOf at this vertex
	 */
	public IsConstraintOf getFirstIsConstraintOf();

	/**
	 * @return the first edge of class IsConstraintOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsConstraintOf getFirstIsConstraintOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsConstraintOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsConstraintOf are accepted
	 */
	public IsConstraintOf getFirstIsConstraintOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsConstraintOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsConstraintOf are accepted
	 */
	public IsConstraintOf getFirstIsConstraintOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsRestrictedExprOf at this vertex
	 */
	public IsRestrictedExprOf getFirstIsRestrictedExprOf();

	/**
	 * @return the first edge of class IsRestrictedExprOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsRestrictedExprOf getFirstIsRestrictedExprOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsRestrictedExprOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsRestrictedExprOf are accepted
	 */
	public IsRestrictedExprOf getFirstIsRestrictedExprOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsRestrictedExprOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsRestrictedExprOf are accepted
	 */
	public IsRestrictedExprOf getFirstIsRestrictedExprOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsRecordExprOf at this vertex
	 */
	public IsRecordExprOf getFirstIsRecordExprOf();

	/**
	 * @return the first edge of class IsRecordExprOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsRecordExprOf getFirstIsRecordExprOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsRecordExprOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsRecordExprOf are accepted
	 */
	public IsRecordExprOf getFirstIsRecordExprOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsRecordExprOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsRecordExprOf are accepted
	 */
	public IsRecordExprOf getFirstIsRecordExprOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsBoundExprOfDefinition at this vertex
	 */
	public IsBoundExprOfDefinition getFirstIsBoundExprOfDefinition();

	/**
	 * @return the first edge of class IsBoundExprOfDefinition at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsBoundExprOfDefinition getFirstIsBoundExprOfDefinition(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsBoundExprOfDefinition at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsBoundExprOfDefinition are accepted
	 */
	public IsBoundExprOfDefinition getFirstIsBoundExprOfDefinition(boolean noSubClasses);

	/**
	 * @return the first edge of class IsBoundExprOfDefinition at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsBoundExprOfDefinition are accepted
	 */
	public IsBoundExprOfDefinition getFirstIsBoundExprOfDefinition(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsPathOf at this vertex
	 */
	public IsPathOf getFirstIsPathOf();

	/**
	 * @return the first edge of class IsPathOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsPathOf getFirstIsPathOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsPathOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsPathOf are accepted
	 */
	public IsPathOf getFirstIsPathOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsPathOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsPathOf are accepted
	 */
	public IsPathOf getFirstIsPathOf(EdgeDirection orientation, boolean noSubClasses);

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
	 * @return the first edge of class IsIdOf at this vertex
	 */
	public IsIdOf getFirstIsIdOf();

	/**
	 * @return the first edge of class IsIdOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsIdOf getFirstIsIdOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsIdOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsIdOf are accepted
	 */
	public IsIdOf getFirstIsIdOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsIdOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsIdOf are accepted
	 */
	public IsIdOf getFirstIsIdOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsFunctionIdOf at this vertex
	 */
	public IsFunctionIdOf getFirstIsFunctionIdOf();

	/**
	 * @return the first edge of class IsFunctionIdOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsFunctionIdOf getFirstIsFunctionIdOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsFunctionIdOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsFunctionIdOf are accepted
	 */
	public IsFunctionIdOf getFirstIsFunctionIdOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsFunctionIdOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsFunctionIdOf are accepted
	 */
	public IsFunctionIdOf getFirstIsFunctionIdOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsQueryExprOf at this vertex
	 */
	public IsQueryExprOf getFirstIsQueryExprOf();

	/**
	 * @return the first edge of class IsQueryExprOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsQueryExprOf getFirstIsQueryExprOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsQueryExprOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsQueryExprOf are accepted
	 */
	public IsQueryExprOf getFirstIsQueryExprOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsQueryExprOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsQueryExprOf are accepted
	 */
	public IsQueryExprOf getFirstIsQueryExprOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsStartExprOf at this vertex
	 */
	public IsStartExprOf getFirstIsStartExprOf();

	/**
	 * @return the first edge of class IsStartExprOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsStartExprOf getFirstIsStartExprOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsStartExprOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsStartExprOf are accepted
	 */
	public IsStartExprOf getFirstIsStartExprOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsStartExprOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsStartExprOf are accepted
	 */
	public IsStartExprOf getFirstIsStartExprOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsEdgeOrVertexExprOf at this vertex
	 */
	public IsEdgeOrVertexExprOf getFirstIsEdgeOrVertexExprOf();

	/**
	 * @return the first edge of class IsEdgeOrVertexExprOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsEdgeOrVertexExprOf getFirstIsEdgeOrVertexExprOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsEdgeOrVertexExprOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsEdgeOrVertexExprOf are accepted
	 */
	public IsEdgeOrVertexExprOf getFirstIsEdgeOrVertexExprOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsEdgeOrVertexExprOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsEdgeOrVertexExprOf are accepted
	 */
	public IsEdgeOrVertexExprOf getFirstIsEdgeOrVertexExprOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsBoundExprOfQuantifier at this vertex
	 */
	public IsBoundExprOfQuantifier getFirstIsBoundExprOfQuantifier();

	/**
	 * @return the first edge of class IsBoundExprOfQuantifier at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsBoundExprOfQuantifier getFirstIsBoundExprOfQuantifier(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsBoundExprOfQuantifier at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsBoundExprOfQuantifier are accepted
	 */
	public IsBoundExprOfQuantifier getFirstIsBoundExprOfQuantifier(boolean noSubClasses);

	/**
	 * @return the first edge of class IsBoundExprOfQuantifier at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsBoundExprOfQuantifier are accepted
	 */
	public IsBoundExprOfQuantifier getFirstIsBoundExprOfQuantifier(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsRowHeaderExprOf at this vertex
	 */
	public IsRowHeaderExprOf getFirstIsRowHeaderExprOf();

	/**
	 * @return the first edge of class IsRowHeaderExprOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsRowHeaderExprOf getFirstIsRowHeaderExprOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsRowHeaderExprOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsRowHeaderExprOf are accepted
	 */
	public IsRowHeaderExprOf getFirstIsRowHeaderExprOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsRowHeaderExprOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsRowHeaderExprOf are accepted
	 */
	public IsRowHeaderExprOf getFirstIsRowHeaderExprOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsRootOf at this vertex
	 */
	public IsRootOf getFirstIsRootOf();

	/**
	 * @return the first edge of class IsRootOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsRootOf getFirstIsRootOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsRootOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsRootOf are accepted
	 */
	public IsRootOf getFirstIsRootOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsRootOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsRootOf are accepted
	 */
	public IsRootOf getFirstIsRootOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsTypeExprOfFunction at this vertex
	 */
	public IsTypeExprOfFunction getFirstIsTypeExprOfFunction();

	/**
	 * @return the first edge of class IsTypeExprOfFunction at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsTypeExprOfFunction getFirstIsTypeExprOfFunction(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsTypeExprOfFunction at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsTypeExprOfFunction are accepted
	 */
	public IsTypeExprOfFunction getFirstIsTypeExprOfFunction(boolean noSubClasses);

	/**
	 * @return the first edge of class IsTypeExprOfFunction at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsTypeExprOfFunction are accepted
	 */
	public IsTypeExprOfFunction getFirstIsTypeExprOfFunction(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsArgumentOf at this vertex
	 */
	public IsArgumentOf getFirstIsArgumentOf();

	/**
	 * @return the first edge of class IsArgumentOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsArgumentOf getFirstIsArgumentOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsArgumentOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsArgumentOf are accepted
	 */
	public IsArgumentOf getFirstIsArgumentOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsArgumentOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsArgumentOf are accepted
	 */
	public IsArgumentOf getFirstIsArgumentOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsCompResultDefOf at this vertex
	 */
	public IsCompResultDefOf getFirstIsCompResultDefOf();

	/**
	 * @return the first edge of class IsCompResultDefOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsCompResultDefOf getFirstIsCompResultDefOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsCompResultDefOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsCompResultDefOf are accepted
	 */
	public IsCompResultDefOf getFirstIsCompResultDefOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsCompResultDefOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsCompResultDefOf are accepted
	 */
	public IsCompResultDefOf getFirstIsCompResultDefOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsLastValueOf at this vertex
	 */
	public IsLastValueOf getFirstIsLastValueOf();

	/**
	 * @return the first edge of class IsLastValueOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsLastValueOf getFirstIsLastValueOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsLastValueOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsLastValueOf are accepted
	 */
	public IsLastValueOf getFirstIsLastValueOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsLastValueOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsLastValueOf are accepted
	 */
	public IsLastValueOf getFirstIsLastValueOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsFirstValueOf at this vertex
	 */
	public IsFirstValueOf getFirstIsFirstValueOf();

	/**
	 * @return the first edge of class IsFirstValueOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsFirstValueOf getFirstIsFirstValueOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsFirstValueOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsFirstValueOf are accepted
	 */
	public IsFirstValueOf getFirstIsFirstValueOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsFirstValueOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsFirstValueOf are accepted
	 */
	public IsFirstValueOf getFirstIsFirstValueOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsEdgeExprOf at this vertex
	 */
	public IsEdgeExprOf getFirstIsEdgeExprOf();

	/**
	 * @return the first edge of class IsEdgeExprOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsEdgeExprOf getFirstIsEdgeExprOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsEdgeExprOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsEdgeExprOf are accepted
	 */
	public IsEdgeExprOf getFirstIsEdgeExprOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsEdgeExprOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsEdgeExprOf are accepted
	 */
	public IsEdgeExprOf getFirstIsEdgeExprOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsTableHeaderOf at this vertex
	 */
	public IsTableHeaderOf getFirstIsTableHeaderOf();

	/**
	 * @return the first edge of class IsTableHeaderOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsTableHeaderOf getFirstIsTableHeaderOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsTableHeaderOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsTableHeaderOf are accepted
	 */
	public IsTableHeaderOf getFirstIsTableHeaderOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsTableHeaderOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsTableHeaderOf are accepted
	 */
	public IsTableHeaderOf getFirstIsTableHeaderOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsColumnHeaderExprOf at this vertex
	 */
	public IsColumnHeaderExprOf getFirstIsColumnHeaderExprOf();

	/**
	 * @return the first edge of class IsColumnHeaderExprOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsColumnHeaderExprOf getFirstIsColumnHeaderExprOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsColumnHeaderExprOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsColumnHeaderExprOf are accepted
	 */
	public IsColumnHeaderExprOf getFirstIsColumnHeaderExprOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsColumnHeaderExprOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsColumnHeaderExprOf are accepted
	 */
	public IsColumnHeaderExprOf getFirstIsColumnHeaderExprOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsPartOf at this vertex
	 */
	public IsPartOf getFirstIsPartOf();

	/**
	 * @return the first edge of class IsPartOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsPartOf getFirstIsPartOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsPartOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsPartOf are accepted
	 */
	public IsPartOf getFirstIsPartOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsPartOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsPartOf are accepted
	 */
	public IsPartOf getFirstIsPartOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsRestrictionOf at this vertex
	 */
	public IsRestrictionOf getFirstIsRestrictionOf();

	/**
	 * @return the first edge of class IsRestrictionOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsRestrictionOf getFirstIsRestrictionOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsRestrictionOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsRestrictionOf are accepted
	 */
	public IsRestrictionOf getFirstIsRestrictionOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsRestrictionOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsRestrictionOf are accepted
	 */
	public IsRestrictionOf getFirstIsRestrictionOf(EdgeDirection orientation, boolean noSubClasses);

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
	 * @return the first edge of class IsTrueExprOf at this vertex
	 */
	public IsTrueExprOf getFirstIsTrueExprOf();

	/**
	 * @return the first edge of class IsTrueExprOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsTrueExprOf getFirstIsTrueExprOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsTrueExprOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsTrueExprOf are accepted
	 */
	public IsTrueExprOf getFirstIsTrueExprOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsTrueExprOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsTrueExprOf are accepted
	 */
	public IsTrueExprOf getFirstIsTrueExprOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsIntermediateVertexOf at this vertex
	 */
	public IsIntermediateVertexOf getFirstIsIntermediateVertexOf();

	/**
	 * @return the first edge of class IsIntermediateVertexOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsIntermediateVertexOf getFirstIsIntermediateVertexOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsIntermediateVertexOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsIntermediateVertexOf are accepted
	 */
	public IsIntermediateVertexOf getFirstIsIntermediateVertexOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsIntermediateVertexOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsIntermediateVertexOf are accepted
	 */
	public IsIntermediateVertexOf getFirstIsIntermediateVertexOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsGoalRestrOf at this vertex
	 */
	public IsGoalRestrOf getFirstIsGoalRestrOf();

	/**
	 * @return the first edge of class IsGoalRestrOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsGoalRestrOf getFirstIsGoalRestrOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsGoalRestrOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsGoalRestrOf are accepted
	 */
	public IsGoalRestrOf getFirstIsGoalRestrOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsGoalRestrOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsGoalRestrOf are accepted
	 */
	public IsGoalRestrOf getFirstIsGoalRestrOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsFalseExprOf at this vertex
	 */
	public IsFalseExprOf getFirstIsFalseExprOf();

	/**
	 * @return the first edge of class IsFalseExprOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsFalseExprOf getFirstIsFalseExprOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsFalseExprOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsFalseExprOf are accepted
	 */
	public IsFalseExprOf getFirstIsFalseExprOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsFalseExprOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsFalseExprOf are accepted
	 */
	public IsFalseExprOf getFirstIsFalseExprOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsNullExprOf at this vertex
	 */
	public IsNullExprOf getFirstIsNullExprOf();

	/**
	 * @return the first edge of class IsNullExprOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsNullExprOf getFirstIsNullExprOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsNullExprOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsNullExprOf are accepted
	 */
	public IsNullExprOf getFirstIsNullExprOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsNullExprOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsNullExprOf are accepted
	 */
	public IsNullExprOf getFirstIsNullExprOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsStartRestrOf at this vertex
	 */
	public IsStartRestrOf getFirstIsStartRestrOf();

	/**
	 * @return the first edge of class IsStartRestrOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsStartRestrOf getFirstIsStartRestrOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsStartRestrOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsStartRestrOf are accepted
	 */
	public IsStartRestrOf getFirstIsStartRestrOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsStartRestrOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsStartRestrOf are accepted
	 */
	public IsStartRestrOf getFirstIsStartRestrOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsTargetExprOf at this vertex
	 */
	public IsTargetExprOf getFirstIsTargetExprOf();

	/**
	 * @return the first edge of class IsTargetExprOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsTargetExprOf getFirstIsTargetExprOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsTargetExprOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsTargetExprOf are accepted
	 */
	public IsTargetExprOf getFirstIsTargetExprOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsTargetExprOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsTargetExprOf are accepted
	 */
	public IsTargetExprOf getFirstIsTargetExprOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsTypeExprOfDeclaration at this vertex
	 */
	public IsTypeExprOfDeclaration getFirstIsTypeExprOfDeclaration();

	/**
	 * @return the first edge of class IsTypeExprOfDeclaration at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsTypeExprOfDeclaration getFirstIsTypeExprOfDeclaration(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsTypeExprOfDeclaration at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsTypeExprOfDeclaration are accepted
	 */
	public IsTypeExprOfDeclaration getFirstIsTypeExprOfDeclaration(boolean noSubClasses);

	/**
	 * @return the first edge of class IsTypeExprOfDeclaration at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsTypeExprOfDeclaration are accepted
	 */
	public IsTypeExprOfDeclaration getFirstIsTypeExprOfDeclaration(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsExprOf at this vertex
	 */
	public IsExprOf getFirstIsExprOf();

	/**
	 * @return the first edge of class IsExprOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsExprOf getFirstIsExprOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsExprOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsExprOf are accepted
	 */
	public IsExprOf getFirstIsExprOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsExprOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsExprOf are accepted
	 */
	public IsExprOf getFirstIsExprOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * @return the first edge of class IsSubgraphOf at this vertex
	 */
	public IsSubgraphOf getFirstIsSubgraphOf();

	/**
	 * @return the first edge of class IsSubgraphOf at this vertex
	 * @param orientation the orientation of the edge
	 */
	public IsSubgraphOf getFirstIsSubgraphOf(EdgeDirection orientation);

	/**
	 * @return the first edge of class IsSubgraphOf at this vertex
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsSubgraphOf are accepted
	 */
	public IsSubgraphOf getFirstIsSubgraphOf(boolean noSubClasses);

	/**
	 * @return the first edge of class IsSubgraphOf at this vertex
	 * @param orientation the orientation of the edge
	 * @param noSubClasses if set to <code>true</code>, no subclasses of IsSubgraphOf are accepted
	 */
	public IsSubgraphOf getFirstIsSubgraphOf(EdgeDirection orientation, boolean noSubClasses);

	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type IsFunctionIdOf or subtypes
	 */
	public Iterable<EdgeVertexPair<? extends IsFunctionIdOf, ? extends Vertex>> getIsFunctionIdOfIncidences();
	
	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type IsFunctionIdOf
	 * @param noSubClasses toggles wether subclasses of IsFunctionIdOf should be excluded
	 */
	public Iterable<EdgeVertexPair<? extends IsFunctionIdOf, ? extends Vertex>> getIsFunctionIdOfIncidences(boolean noSubClasses);
	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type IsFunctionIdOf
	 * @param direction EdgeDirection.IN or EdgeDirection.OUT, only edges of this direction will be included in the iterable
	 * @param noSubClasses toggles wether subclasses of IsFunctionIdOf should be excluded
	 */
	public Iterable<EdgeVertexPair<? extends IsFunctionIdOf, ? extends Vertex>> getIsFunctionIdOfIncidences(EdgeDirection direction, boolean noSubClasses);
	
	/**
	 * Returns an iterable for all incidence edges of this vertex that are of type IsFunctionIdOf
	 * @param direction EdgeDirection.IN or EdgeDirection.OUT, only edges of this direction will be included in the iterable
	 */
	public Iterable<EdgeVertexPair<? extends IsFunctionIdOf, ? extends Vertex>> getIsFunctionIdOfIncidences(EdgeDirection direction);

}
