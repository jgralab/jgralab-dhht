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
import de.uni_koblenz.jgralab.greql2.schema.IsBoundVarOf;
import de.uni_koblenz.jgralab.greql2.schema.SourcePosition;

import java.io.IOException;

public class ReversedIsBoundVarOfImpl extends ReversedAggregationImpl implements Aggregation, Greql2Aggregation, IsBoundVarOf {

	ReversedIsBoundVarOfImpl(EdgeImpl e, Graph g) {
		super(e, g);
	}

	public Object getAttribute(String attributeName) throws NoSuchFieldException {
		return ((IsBoundVarOf)normalEdge).getAttribute(attributeName);
	}

	public void setAttribute(String attributeName, Object data) throws NoSuchFieldException {
		((IsBoundVarOf)normalEdge).setAttribute(attributeName, data);
	}

	public java.util.List<SourcePosition> getSourcePositions() {
		return ((IsBoundVarOf)normalEdge).getSourcePositions();
	}

	public void setSourcePositions(java.util.List<SourcePosition> sourcePositions) {
		((IsBoundVarOf)normalEdge).setSourcePositions(sourcePositions);
	}

	public void readAttributeValues(GraphIO io) throws GraphIOException {
	}

	public void writeAttributeValues(GraphIO io) throws GraphIOException, IOException {
	}

	public Greql2Aggregation getNextGreql2AggregationInGraph() {
		return ((Greql2Aggregation)normalEdge).getNextGreql2AggregationInGraph();
	}

	public IsBoundVarOf getNextIsBoundVarOfInGraph() {
		return ((IsBoundVarOf)normalEdge).getNextIsBoundVarOfInGraph();
	}

	public IsBoundVarOf getNextIsBoundVarOfInGraph(boolean noSubClasses) {
		return ((IsBoundVarOf)normalEdge).getNextIsBoundVarOfInGraph(noSubClasses);
	}

	public Greql2Aggregation getNextGreql2Aggregation() {
		return (Greql2Aggregation)getNextEdgeOfClass(Greql2Aggregation.class);
	}

	public Greql2Aggregation getNextGreql2Aggregation(EdgeDirection orientation) {
		return (Greql2Aggregation)getNextEdgeOfClass(Greql2Aggregation.class, orientation);
	}

	public IsBoundVarOf getNextIsBoundVarOf() {
		return (IsBoundVarOf)getNextEdgeOfClass(IsBoundVarOf.class);
	}

	public IsBoundVarOf getNextIsBoundVarOf(EdgeDirection orientation) {
		return (IsBoundVarOf)getNextEdgeOfClass(IsBoundVarOf.class, orientation);
	}

	public IsBoundVarOf getNextIsBoundVarOf(boolean noSubClasses) {
		return (IsBoundVarOf)getNextEdgeOfClass(IsBoundVarOf.class, noSubClasses);
	}

	public IsBoundVarOf getNextIsBoundVarOf(EdgeDirection orientation, boolean noSubClasses) {
		return (IsBoundVarOf)getNextEdgeOfClass(IsBoundVarOf.class, orientation, noSubClasses);
	}

}
