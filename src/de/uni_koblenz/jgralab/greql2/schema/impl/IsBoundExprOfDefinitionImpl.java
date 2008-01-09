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

import de.uni_koblenz.jgralab.greql2.schema.impl.ReversedIsBoundExprOfDefinitionImpl;

import de.uni_koblenz.jgralab.greql2.schema.Greql2Aggregation;
import de.uni_koblenz.jgralab.greql2.schema.IsBoundExprOf;
import de.uni_koblenz.jgralab.greql2.schema.IsBoundExprOfDefinition;
import de.uni_koblenz.jgralab.greql2.schema.SourcePosition;

import java.io.IOException;
/**
FromVertexClass: Expression
FromRoleName : 
ToVertexClass: DefinitionExpression
toRoleName : 
 */

public class IsBoundExprOfDefinitionImpl extends AggregationImpl implements Aggregation, Greql2Aggregation, IsBoundExprOf, IsBoundExprOfDefinition {

	protected java.util.List<SourcePosition> sourcePositions;

	public IsBoundExprOfDefinitionImpl(int id, Graph g) {
		super(id, g, (AggregationClass)g.getGraphClass().getGraphElementClass("IsBoundExprOfDefinition"));
		reversedEdge = new ReversedIsBoundExprOfDefinitionImpl(this, g);
	}

	public Class<? extends AttributedElement> getM1Class() {
		return IsBoundExprOfDefinition.class;
	}

	public Object getAttribute(String attributeName) throws NoSuchFieldException {
		if (attributeName.equals("sourcePositions")) return sourcePositions;
		throw new NoSuchFieldException("IsBoundExprOfDefinition doesn't contain an attribute " + attributeName);
	}

	@SuppressWarnings("unchecked")
	public void setAttribute(String attributeName, Object data) throws NoSuchFieldException {
		if (attributeName.equals("sourcePositions")) {
			setSourcePositions((java.util.List<SourcePosition>) data);
			return;
		}
		throw new NoSuchFieldException("IsBoundExprOfDefinition doesn't contain an attribute " + attributeName);
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

	public IsBoundExprOf getNextIsBoundExprOfInGraph() {
		return (IsBoundExprOf)getNextEdgeOfClassInGraph(IsBoundExprOf.class);
	}

	public IsBoundExprOfDefinition getNextIsBoundExprOfDefinitionInGraph() {
		return (IsBoundExprOfDefinition)getNextEdgeOfClassInGraph(IsBoundExprOfDefinition.class);
	}

	public IsBoundExprOfDefinition getNextIsBoundExprOfDefinitionInGraph(boolean noSubClasses) {
		return (IsBoundExprOfDefinition)getNextEdgeOfClassInGraph(IsBoundExprOfDefinition.class, noSubClasses);
	}

	public Greql2Aggregation getNextGreql2Aggregation() {
		return (Greql2Aggregation)getNextEdgeOfClass(Greql2Aggregation.class);
	}

	public Greql2Aggregation getNextGreql2Aggregation(EdgeDirection orientation) {
		return (Greql2Aggregation)getNextEdgeOfClass(Greql2Aggregation.class, orientation);
	}

	public IsBoundExprOf getNextIsBoundExprOf() {
		return (IsBoundExprOf)getNextEdgeOfClass(IsBoundExprOf.class);
	}

	public IsBoundExprOf getNextIsBoundExprOf(EdgeDirection orientation) {
		return (IsBoundExprOf)getNextEdgeOfClass(IsBoundExprOf.class, orientation);
	}

	public IsBoundExprOfDefinition getNextIsBoundExprOfDefinition() {
		return (IsBoundExprOfDefinition)getNextEdgeOfClass(IsBoundExprOfDefinition.class);
	}

	public IsBoundExprOfDefinition getNextIsBoundExprOfDefinition(EdgeDirection orientation) {
		return (IsBoundExprOfDefinition)getNextEdgeOfClass(IsBoundExprOfDefinition.class, orientation);
	}

	public IsBoundExprOfDefinition getNextIsBoundExprOfDefinition(boolean noSubClasses) {
		return (IsBoundExprOfDefinition)getNextEdgeOfClass(IsBoundExprOfDefinition.class, noSubClasses);
	}

	public IsBoundExprOfDefinition getNextIsBoundExprOfDefinition(EdgeDirection orientation, boolean noSubClasses) {
		return (IsBoundExprOfDefinition)getNextEdgeOfClass(IsBoundExprOfDefinition.class, orientation, noSubClasses);
	}

}
