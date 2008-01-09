/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.greql2.schema.impl;

import de.uni_koblenz.jgralab.impl.array.AggregationImpl;

import de.uni_koblenz.jgralab.AggregationClass;
import de.uni_koblenz.jgralab.Aggregation;
import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;

import de.uni_koblenz.jgralab.greql2.schema.impl.ReversedIsRestrictedExprOfImpl;

import de.uni_koblenz.jgralab.greql2.schema.Greql2Aggregation;
import de.uni_koblenz.jgralab.greql2.schema.IsRestrictedExprOf;
import de.uni_koblenz.jgralab.greql2.schema.SourcePosition;

import java.io.IOException;
/**
FromVertexClass: Expression
FromRoleName : 
ToVertexClass: RestrictedExpression
toRoleName : 
 */

public class IsRestrictedExprOfImpl extends AggregationImpl implements Aggregation, Greql2Aggregation, IsRestrictedExprOf {

	protected java.util.List<SourcePosition> sourcePositions;

	public IsRestrictedExprOfImpl(int id, Graph g) {
		super(id, g, (AggregationClass)g.getGraphClass().getGraphElementClass("IsRestrictedExprOf"));
		reversedEdge = new ReversedIsRestrictedExprOfImpl(this, g);
	}

	public Class<? extends AttributedElement> getM1Class() {
		return IsRestrictedExprOf.class;
	}

	public Object getAttribute(String attributeName) throws NoSuchFieldException {
		if (attributeName.equals("sourcePositions")) return sourcePositions;
		throw new NoSuchFieldException("IsRestrictedExprOf doesn't contain an attribute " + attributeName);
	}

	@SuppressWarnings("unchecked")
	public void setAttribute(String attributeName, Object data) throws NoSuchFieldException {
		if (attributeName.equals("sourcePositions")) {
			setSourcePositions((java.util.List<SourcePosition>) data);
			return;
		}
		throw new NoSuchFieldException("IsRestrictedExprOf doesn't contain an attribute " + attributeName);
	}

	public java.util.List<SourcePosition> getSourcePositions() {
		return sourcePositions;
	}

	public void setSourcePositions(java.util.List<SourcePosition> sourcePositions) {
		this.sourcePositions = sourcePositions;
		modified();
	}

	public void readAttributeValues(GraphIO io) throws GraphIOException {
		if (!io.isNextToken("\\null")) {
			sourcePositions = new java.util.ArrayList<SourcePosition>();
			io.match("[");
			while (!io.isNextToken("]")) {
				SourcePosition sourcePositionsElement;
				if (!io.isNextToken("\\null")) {
				    sourcePositionsElement = new SourcePosition(io);
				} else {
				    io.match("\\null");
				    sourcePositionsElement = null;
				}
				sourcePositions.add(sourcePositionsElement);
			}
			io.match("]");
		} else {
			io.match("\\null");
			sourcePositions = null;
		}
		setSourcePositions(sourcePositions);
	}

	public void writeAttributeValues(GraphIO io) throws GraphIOException, IOException {
		io.space();
		if (sourcePositions != null) {
			io.writeSpace();
			io.write("[");
			io.noSpace();
			for (SourcePosition sourcePositionsElement: sourcePositions) {
				if (sourcePositionsElement != null) {
				    sourcePositionsElement.writeComponentValues(io);
				} else {
				    io.writeIdentifier("\\null");
				}
			}
			io.write("]");
		} else {
			io.writeIdentifier("\\null");
		}
	}

	public Greql2Aggregation getNextGreql2AggregationInGraph() {
		return (Greql2Aggregation)getNextEdgeOfClassInGraph(Greql2Aggregation.class);
	}

	public IsRestrictedExprOf getNextIsRestrictedExprOfInGraph() {
		return (IsRestrictedExprOf)getNextEdgeOfClassInGraph(IsRestrictedExprOf.class);
	}

	public IsRestrictedExprOf getNextIsRestrictedExprOfInGraph(boolean noSubClasses) {
		return (IsRestrictedExprOf)getNextEdgeOfClassInGraph(IsRestrictedExprOf.class, noSubClasses);
	}

	public Greql2Aggregation getNextGreql2Aggregation() {
		return (Greql2Aggregation)getNextEdgeOfClass(Greql2Aggregation.class);
	}

	public Greql2Aggregation getNextGreql2Aggregation(EdgeDirection orientation) {
		return (Greql2Aggregation)getNextEdgeOfClass(Greql2Aggregation.class, orientation);
	}

	public IsRestrictedExprOf getNextIsRestrictedExprOf() {
		return (IsRestrictedExprOf)getNextEdgeOfClass(IsRestrictedExprOf.class);
	}

	public IsRestrictedExprOf getNextIsRestrictedExprOf(EdgeDirection orientation) {
		return (IsRestrictedExprOf)getNextEdgeOfClass(IsRestrictedExprOf.class, orientation);
	}

	public IsRestrictedExprOf getNextIsRestrictedExprOf(boolean noSubClasses) {
		return (IsRestrictedExprOf)getNextEdgeOfClass(IsRestrictedExprOf.class, noSubClasses);
	}

	public IsRestrictedExprOf getNextIsRestrictedExprOf(EdgeDirection orientation, boolean noSubClasses) {
		return (IsRestrictedExprOf)getNextEdgeOfClass(IsRestrictedExprOf.class, orientation, noSubClasses);
	}

}
