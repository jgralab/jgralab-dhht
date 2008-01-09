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
import de.uni_koblenz.jgralab.greql2.schema.IsCompDeclOf;
import de.uni_koblenz.jgralab.greql2.schema.SourcePosition;

import java.io.IOException;

public class ReversedIsCompDeclOfImpl extends ReversedAggregationImpl implements Aggregation, Greql2Aggregation, IsCompDeclOf {

	ReversedIsCompDeclOfImpl(EdgeImpl e, Graph g) {
		super(e, g);
	}

	public Object getAttribute(String attributeName) throws NoSuchFieldException {
		return ((IsCompDeclOf)normalEdge).getAttribute(attributeName);
	}

	public void setAttribute(String attributeName, Object data) throws NoSuchFieldException {
		((IsCompDeclOf)normalEdge).setAttribute(attributeName, data);
	}

	public java.util.List<SourcePosition> getSourcePositions() {
		return ((IsCompDeclOf)normalEdge).getSourcePositions();
	}

	public void setSourcePositions(java.util.List<SourcePosition> sourcePositions) {
		((IsCompDeclOf)normalEdge).setSourcePositions(sourcePositions);
	}

	public void readAttributeValues(GraphIO io) throws GraphIOException {
	}

	public void writeAttributeValues(GraphIO io) throws GraphIOException, IOException {
	}

	public Greql2Aggregation getNextGreql2AggregationInGraph() {
		return ((Greql2Aggregation)normalEdge).getNextGreql2AggregationInGraph();
	}

	public IsCompDeclOf getNextIsCompDeclOfInGraph() {
		return ((IsCompDeclOf)normalEdge).getNextIsCompDeclOfInGraph();
	}

	public IsCompDeclOf getNextIsCompDeclOfInGraph(boolean noSubClasses) {
		return ((IsCompDeclOf)normalEdge).getNextIsCompDeclOfInGraph(noSubClasses);
	}

	public Greql2Aggregation getNextGreql2Aggregation() {
		return (Greql2Aggregation)getNextEdgeOfClass(Greql2Aggregation.class);
	}

	public Greql2Aggregation getNextGreql2Aggregation(EdgeDirection orientation) {
		return (Greql2Aggregation)getNextEdgeOfClass(Greql2Aggregation.class, orientation);
	}

	public IsCompDeclOf getNextIsCompDeclOf() {
		return (IsCompDeclOf)getNextEdgeOfClass(IsCompDeclOf.class);
	}

	public IsCompDeclOf getNextIsCompDeclOf(EdgeDirection orientation) {
		return (IsCompDeclOf)getNextEdgeOfClass(IsCompDeclOf.class, orientation);
	}

	public IsCompDeclOf getNextIsCompDeclOf(boolean noSubClasses) {
		return (IsCompDeclOf)getNextEdgeOfClass(IsCompDeclOf.class, noSubClasses);
	}

	public IsCompDeclOf getNextIsCompDeclOf(EdgeDirection orientation, boolean noSubClasses) {
		return (IsCompDeclOf)getNextEdgeOfClass(IsCompDeclOf.class, orientation, noSubClasses);
	}

}
