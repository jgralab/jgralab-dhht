/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2011 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
 * 
 * For bug reports, documentation and further information, visit
 * 
 *                         http://jgralab.uni-koblenz.de
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 3 of the License, or (at your
 * option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 * 
 * Additional permission under GNU GPL version 3 section 7
 * 
 * If you modify this Program, or any covered work, by linking or combining
 * it with Eclipse (or a modified version of that program or an Eclipse
 * plugin), containing parts covered by the terms of the Eclipse Public
 * License (EPL), the licensors of this Program grant you additional
 * permission to convey the resulting work.  Corresponding Source for a
 * non-source form of such a combination shall include the source code for
 * the parts of JGraLab used as well as that of the covered work.
 */

package de.uni_koblenz.jgralab.greql2.evaluator.fa;


import java.rmi.RemoteException;

import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.GraphElement;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.graphmarker.ObjectGraphMarker;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.ThisVertexEvaluator;
import de.uni_koblenz.jgralab.greql2.evaluator.vertexeval.VertexEvaluator;
import de.uni_koblenz.jgralab.greql2.schema.ThisVertex;
import de.uni_koblenz.jgralab.greql2.serialising.GreqlSerializer;

/**
 * This transition may fire, if the VertexEvaluator it holds as attribute
 * returns true as result
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class BoolExpressionTransition extends Transition {

	private VertexEvaluator boolExpressionEvaluator;

	private ThisVertexEvaluator thisVertexEvaluator;

	/**
	 * returns a string which describes the edge
	 */
	@Override
	public String edgeString() {
		String desc = "BoolExpressionTransition";
		return desc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * greql2.evaluator.fa.Transition#equalSymbol(greql2.evaluator.fa.EdgeTransition
	 * )
	 */
	@Override
	public boolean equalSymbol(Transition t) {
		if (!(t instanceof BoolExpressionTransition)) {
			return false;
		}
		BoolExpressionTransition bt = (BoolExpressionTransition) t;
		if (bt.boolExpressionEvaluator == this.boolExpressionEvaluator) {
			return true;
		}
		return false;
	}

	/**
	 * Copy-constructor, creates a copy of the given transition
	 */
	protected BoolExpressionTransition(BoolExpressionTransition t,
			boolean addToStates) {
		super(t, addToStates);
		boolExpressionEvaluator = t.boolExpressionEvaluator;
		thisVertexEvaluator = t.thisVertexEvaluator;
	}

	/**
	 * returns a copy of this transition
	 */
	@Override
	public Transition copy(boolean addToStates) {
		return new BoolExpressionTransition(this, addToStates);
	}

	/**
	 * Creates a new transition from start state to end state.
	 */
	public BoolExpressionTransition(State start, State end,
			VertexEvaluator boolEval,
			ObjectGraphMarker<Vertex, VertexEvaluator> graphMarker) {
		super(start, end);
		boolExpressionEvaluator = boolEval;
		Vertex v;
		try {
			v = graphMarker.getGraph().getFirstVertex(ThisVertex.class);
			if (v != null) {
				thisVertexEvaluator = (ThisVertexEvaluator) graphMarker
						.getMark(v);
			}
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see greql2.evaluator.fa.Transition#isEpsilon()
	 */
	@Override
	public boolean isEpsilon() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see greql2.evaluator.fa.Transition#accepts(jgralab.Vertex, jgralab.Edge)
	 */
	@Deprecated
	public boolean accepts(Vertex v, Edge e) {
		if (thisVertexEvaluator != null) {
			thisVertexEvaluator.setValue(v);
		}
		Object res = boolExpressionEvaluator.getResult();
		if (res instanceof Boolean && ((Boolean) res).equals(Boolean.TRUE)) {
			return true;
		}
		return false;
	}

	@Deprecated
	public Vertex getNextElement(Vertex v, Edge e) {
		return v;
	}

	@Override
	public String prettyPrint() {
		return "IntermediateVertex "
				+ GreqlSerializer.serializeVertex(boolExpressionEvaluator
						.getVertex());
	}

	@Override
	public boolean consumesIncidence() {
		return false;
	}

	@Override
	public boolean accepts(GraphElement e, Incidence i) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GraphElement getNextElement(
			GraphElement elem, Incidence inc) {
		// TODO Auto-generated method stub
		return null;
	}
}
