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
import de.uni_koblenz.jgralab.greql2.schema.IsAlternativePathOf;
import de.uni_koblenz.jgralab.greql2.schema.IsPathDescriptionOf;
import de.uni_koblenz.jgralab.greql2.schema.SourcePosition;

import java.io.IOException;

public class ReversedIsAlternativePathOfImpl extends ReversedAggregationImpl implements Aggregation, Greql2Aggregation, IsAlternativePathOf, IsPathDescriptionOf {

	ReversedIsAlternativePathOfImpl(EdgeImpl e, Graph g) {
		super(e, g);
	}

	public Object getAttribute(String attributeName) throws NoSuchFieldException {
		return ((IsAlternativePathOf)normalEdge).getAttribute(attributeName);
	}

	public void setAttribute(String attributeName, Object data) throws NoSuchFieldException {
		((IsAlternativePathOf)normalEdge).setAttribute(attributeName, data);
	}

	public java.util.List<SourcePosition> getSourcePositions() {
		return ((IsAlternativePathOf)normalEdge).getSourcePositions();
	}

	public void setSourcePositions(java.util.List<SourcePosition> sourcePositions) {
		((IsAlternativePathOf)normalEdge).setSourcePositions(sourcePositions);
	}

	public void readAttributeValues(GraphIO io) throws GraphIOException {
	}

	public void writeAttributeValues(GraphIO io) throws GraphIOException, IOException {
	}

	public Greql2Aggregation getNextGreql2AggregationInGraph() {
		return ((Greql2Aggregation)normalEdge).getNextGreql2AggregationInGraph();
	}

	public IsAlternativePathOf getNextIsAlternativePathOfInGraph() {
		return ((IsAlternativePathOf)normalEdge).getNextIsAlternativePathOfInGraph();
	}

	public IsAlternativePathOf getNextIsAlternativePathOfInGraph(boolean noSubClasses) {
		return ((IsAlternativePathOf)normalEdge).getNextIsAlternativePathOfInGraph(noSubClasses);
	}

	public IsPathDescriptionOf getNextIsPathDescriptionOfInGraph() {
		return ((IsPathDescriptionOf)normalEdge).getNextIsPathDescriptionOfInGraph();
	}

	public Greql2Aggregation getNextGreql2Aggregation() {
		return (Greql2Aggregation)getNextEdgeOfClass(Greql2Aggregation.class);
	}

	public Greql2Aggregation getNextGreql2Aggregation(EdgeDirection orientation) {
		return (Greql2Aggregation)getNextEdgeOfClass(Greql2Aggregation.class, orientation);
	}

	public IsAlternativePathOf getNextIsAlternativePathOf() {
		return (IsAlternativePathOf)getNextEdgeOfClass(IsAlternativePathOf.class);
	}

	public IsAlternativePathOf getNextIsAlternativePathOf(EdgeDirection orientation) {
		return (IsAlternativePathOf)getNextEdgeOfClass(IsAlternativePathOf.class, orientation);
	}

	public IsAlternativePathOf getNextIsAlternativePathOf(boolean noSubClasses) {
		return (IsAlternativePathOf)getNextEdgeOfClass(IsAlternativePathOf.class, noSubClasses);
	}

	public IsAlternativePathOf getNextIsAlternativePathOf(EdgeDirection orientation, boolean noSubClasses) {
		return (IsAlternativePathOf)getNextEdgeOfClass(IsAlternativePathOf.class, orientation, noSubClasses);
	}

	public IsPathDescriptionOf getNextIsPathDescriptionOf() {
		return (IsPathDescriptionOf)getNextEdgeOfClass(IsPathDescriptionOf.class);
	}

	public IsPathDescriptionOf getNextIsPathDescriptionOf(EdgeDirection orientation) {
		return (IsPathDescriptionOf)getNextEdgeOfClass(IsPathDescriptionOf.class, orientation);
	}

}
