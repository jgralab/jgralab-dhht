/*
 * This code was generated automatically.
 * Do NOT edit this file, changes will be lost.
 * Instead, change and commit the underlying schema.
 */

package de.uni_koblenz.jgralab.greql2.schema.impl;

import de.uni_koblenz.jgralab.impl.array.ReversedAggregationImpl;
import de.uni_koblenz.jgralab.impl.array.EdgeImpl;

import de.uni_koblenz.jgralab.Aggregation;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;

import de.uni_koblenz.jgralab.greql2.schema.Greql2Aggregation;
import de.uni_koblenz.jgralab.greql2.schema.IsFirstValueOf;
import de.uni_koblenz.jgralab.greql2.schema.SourcePosition;

import java.io.IOException;

public class ReversedIsFirstValueOfImpl extends ReversedAggregationImpl implements Aggregation, Greql2Aggregation, IsFirstValueOf {

	ReversedIsFirstValueOfImpl(EdgeImpl e, Graph g) {
		super(e, g);
	}

	public Object getAttribute(String attributeName) throws NoSuchFieldException {
		return ((IsFirstValueOf)normalEdge).getAttribute(attributeName);
	}

	public void setAttribute(String attributeName, Object data) throws NoSuchFieldException {
		((IsFirstValueOf)normalEdge).setAttribute(attributeName, data);
	}

	public java.util.List<SourcePosition> getSourcePositions() {
		return ((IsFirstValueOf)normalEdge).getSourcePositions();
	}

	public void setSourcePositions(java.util.List<SourcePosition> sourcePositions) {
		((IsFirstValueOf)normalEdge).setSourcePositions(sourcePositions);
	}

	public void readAttributeValues(GraphIO io) throws GraphIOException {
	}

	public void writeAttributeValues(GraphIO io) throws GraphIOException, IOException {
	}

	public Greql2Aggregation getNextGreql2AggregationInGraph() {
		return ((Greql2Aggregation)normalEdge).getNextGreql2AggregationInGraph();
	}

	public IsFirstValueOf getNextIsFirstValueOfInGraph() {
		return ((IsFirstValueOf)normalEdge).getNextIsFirstValueOfInGraph();
	}

	public IsFirstValueOf getNextIsFirstValueOfInGraph(boolean noSubClasses) {
		return ((IsFirstValueOf)normalEdge).getNextIsFirstValueOfInGraph(noSubClasses);
	}

	public Greql2Aggregation getNextGreql2Aggregation() {
		return (Greql2Aggregation)getNextEdgeOfClass(Greql2Aggregation.class);
	}

	public Greql2Aggregation getNextGreql2Aggregation(EdgeDirection orientation) {
		return (Greql2Aggregation)getNextEdgeOfClass(Greql2Aggregation.class, orientation);
	}

	public IsFirstValueOf getNextIsFirstValueOf() {
		return (IsFirstValueOf)getNextEdgeOfClass(IsFirstValueOf.class);
	}

	public IsFirstValueOf getNextIsFirstValueOf(EdgeDirection orientation) {
		return (IsFirstValueOf)getNextEdgeOfClass(IsFirstValueOf.class, orientation);
	}

	public IsFirstValueOf getNextIsFirstValueOf(boolean noSubClasses) {
		return (IsFirstValueOf)getNextEdgeOfClass(IsFirstValueOf.class, noSubClasses);
	}

	public IsFirstValueOf getNextIsFirstValueOf(EdgeDirection orientation, boolean noSubClasses) {
		return (IsFirstValueOf)getNextEdgeOfClass(IsFirstValueOf.class, orientation, noSubClasses);
	}

}
