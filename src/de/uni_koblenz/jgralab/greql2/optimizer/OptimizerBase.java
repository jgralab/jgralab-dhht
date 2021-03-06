/*
 * JGraLab - The Java Graph Laboratory
 * 
 * Copyright (C) 2006-2010 Institute for Software Technology
 *                         University of Koblenz-Landau, Germany
 *                         ist@uni-koblenz.de
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
/**
 *
 */
package de.uni_koblenz.jgralab.greql2.optimizer;

import java.util.HashSet;
import java.util.Set;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Edge;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.exception.GreqlException;
import de.uni_koblenz.jgralab.greql2.exception.OptimizerException;
import de.uni_koblenz.jgralab.greql2.schema.Declaration;
import de.uni_koblenz.jgralab.greql2.schema.Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Expression;
import de.uni_koblenz.jgralab.greql2.schema.GreqlSyntaxGraph;
import de.uni_koblenz.jgralab.greql2.schema.IsBoundVarOf;
import de.uni_koblenz.jgralab.greql2.schema.IsBoundVarOf_boundVar;
import de.uni_koblenz.jgralab.greql2.schema.IsDeclaredVarOf;
import de.uni_koblenz.jgralab.greql2.schema.IsDeclaredVarOf_isDeclaredVarOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.IsSimpleDeclOf;
import de.uni_koblenz.jgralab.greql2.schema.IsSimpleDeclOf_isSimpleDeclOf_omega;
import de.uni_koblenz.jgralab.greql2.schema.SimpleDeclaration;
import de.uni_koblenz.jgralab.greql2.schema.Variable;

/**
 * Base class for all {@link Optimizer}s which defines some useful methods that
 * are needed in derived Classes.
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public abstract class OptimizerBase implements Optimizer {

	protected String optimizerHeaderString() {
		return "*** " + this.getClass().getSimpleName() + ": ";
	}

	protected void recreateVertexEvaluators(GreqlEvaluator eval) {
		try {
			eval.createVertexEvaluators();
		} catch (GreqlException e) {
			e.printStackTrace();
			throw new OptimizerException(
					"Exception while re-creating VertexEvaluators.", e);
		}
	}

	/**
	 * Put all edges going in or coming out of vertex <code>from</code> to
	 * vertex <code>to</code>. If there's already an edge of exactly that type
	 * between <code>from</code>'s that-vertex and <code>to</code>, then don't
	 * create a duplicate edge, unless <code>allowDuplicateEdges</code> is true.
	 * 
	 * @param from
	 *            the old vertex
	 * @param to
	 *            the new vertex
	 * @param allowDuplicateEdges
	 */
	protected void relink(Vertex from, Vertex to) {
		assert (from != null) && (to != null) : "Relinking null!";
		assert from != to : "Relinking from itself!";
		assert from.getM1Class() == to.getM1Class() : "Relinking different classes! from is "
				+ from + ", to is " + to;
		assert from.isValid() && to.isValid() : "Relinking invalid vertices!";

		// System.out.println("    relink: " + from + " --> " + to);
		Incidence inc = from.getFirstIncidence(Direction.EDGE_TO_VERTEX);
		while (inc != null) {
			Edge e = inc.getEdge();
			inc = inc.getNextIncidenceAtVertex(Direction.EDGE_TO_VERTEX);
			((BinaryEdge) e).setOmega(to);
		}
		inc = from.getFirstIncidence(Direction.VERTEX_TO_EDGE);
		while (inc != null) {
			Edge e = inc.getEdge();
			inc = inc.getNextIncidenceAtVertex(Direction.VERTEX_TO_EDGE);
			((BinaryEdge) e).setAlpha(to);
		}
	}

	/**
	 * Check if <code>var1</code> is declared before <code>var2</code>. A
	 * {@link Variable} is declared before another variable, if it's declared in
	 * an outer {@link Declaration}, or if it's declared in the same
	 * {@link Declaration} but in a {@link SimpleDeclaration} that comes before
	 * the other {@link Variable}'s {@link SimpleDeclaration}, or if it's
	 * declared in the same {@link SimpleDeclaration} but is connected to that
	 * earlier (meaning its {@link IsDeclaredVarOf} edge comes before the
	 * other's).
	 * 
	 * Note that a {@link Variable} is never declared before itself.
	 * 
	 * @param var1
	 *            a {@link Variable}
	 * @param var2
	 *            a {@link Variable}
	 * @return <code>true</code> if <code>var1</code> is declared before
	 *         <code>var2</code>, <code>false</code> otherwise.
	 */
	protected boolean isDeclaredBefore(Variable var1, Variable var2) {
		// GreqlEvaluator.println("isDeclaredBefore(" + var1 + ", " + var2 +
		// ")");
		if (var1 == var2) {
			return false;
		}
		IsBoundVarOf_boundVar ibvo1 = var1.getFirstIncidenceToIsBoundVarOf();
		IsBoundVarOf_boundVar ibvo2 = var2.getFirstIncidenceToIsBoundVarOf();

		if (ibvo1 != null) {
			if (ibvo2 == null) {
				// Externally bound vars are always before locally declared vars
				return true;
			}
			Greql2Expression root = (Greql2Expression) ibvo1.getEdge().getOmega();
			for (IsBoundVarOf ibvo : root.getIncidentEdgesOfType_IsBoundVarOf()) {
				if (ibvo == ibvo1.getEdge()) {
					return true;
				} else if (ibvo == ibvo2.getEdge()) {
					return false;
				}
			}
			throw new OptimizerException("You must never come here...");
		} else if (ibvo2 != null) {
			// Only var2 is externally bound.
			return false;
		}

		SimpleDeclaration sd1 = (SimpleDeclaration) var1
				.getFirstIncidenceToIsDeclaredVarOf(Direction.VERTEX_TO_EDGE).getEdge().getOmega();
		Declaration decl1 = (Declaration) sd1.getFirstIncidenceToIsSimpleDeclOf(
				Direction.VERTEX_TO_EDGE).getEdge().getOmega();
		SimpleDeclaration sd2 = (SimpleDeclaration) var2
				.getFirstIncidenceToIsDeclaredVarOf(Direction.VERTEX_TO_EDGE).getEdge().getOmega();
		Declaration decl2 = (Declaration) sd2.getFirstIncidenceToIsSimpleDeclOf(Direction.VERTEX_TO_EDGE).getEdge().getOmega();

		if (decl1 == decl2) {
			if (sd1 == sd2) {
				// var1 and var2 are declared in the same SimpleDeclaration,
				// so the order of the IsDeclaredVarOf edges matters.
				IsDeclaredVarOf_isDeclaredVarOf_omega inc = sd1
						.getFirstIncidenceToIsDeclaredVarOf(Direction.EDGE_TO_VERTEX);
				while (inc != null) {
					if (inc.getEdge().getAlpha() == var1) {
						return true;
					}
					if (inc.getEdge().getAlpha() == var2) {
						return false;
					}
					inc = inc.getNextIsDeclaredVarOf_omegaAtVertex();
				}
			} else {
				// var1 and var2 are declared in the same Declaration but
				// different SimpleDeclarations, so the order of the
				// SimpleDeclarations matters.
				IsSimpleDeclOf_isSimpleDeclOf_omega inc = decl1
						.getFirstIncidenceToIsSimpleDeclOf(Direction.EDGE_TO_VERTEX);
				while (inc != null) {
					if (inc.getEdge().getAlpha() == sd1) {
						return true;
					}
					if (inc.getEdge().getAlpha() == sd2) {
						return false;
					}
					inc = inc.getNextIsSimpleDeclOf_omegaAtVertex();
				}
			}
		} else {
			// start and target are declared in different Declarations, so we
			// have to check if start was declared in the outer Declaration.
			Vertex declParent1 = ((BinaryEdge) decl1.getFirstIncidence(Direction.VERTEX_TO_EDGE)
					.getEdge()).getOmega();
			Vertex declParent2 = ((BinaryEdge) decl2.getFirstIncidence(Direction.VERTEX_TO_EDGE)
					.getEdge()).getOmega();
			if (OptimizerUtility.isAbove(declParent1, declParent2)) {
				return true;
			} else {
				return false;
			}
		}
		throw new OptimizerException(
				"No case matched in isDeclaredBefore(Variable, Variable)."
						+ " That must not happen!");
	}

	/**
	 * Find the nearest {@link Declaration} above <code>vertex</code>.
	 * 
	 * @param vertex
	 *            a {@link Vertex}
	 * @return nearest {@link Declaration} above <code>vertex</code>
	 */
	protected Declaration findNearestDeclarationAbove(Vertex vertex) {
		if (vertex instanceof Declaration) {
			return (Declaration) vertex;
		}
		Declaration result = null;
		Incidence inc = vertex.getFirstIncidence(Direction.VERTEX_TO_EDGE);
		while (inc != null) {
			result = findNearestDeclarationAbove(((BinaryEdge) inc.getEdge()).getOmega());
			if (result != null) {
				return result;
			}
			inc = inc.getNextIncidenceAtVertex(Direction.VERTEX_TO_EDGE);
		}
		return null;
	}

	/**
	 * Split the given {@link SimpleDeclaration} so that there's one
	 * {@link SimpleDeclaration} that declares the {@link Variable}s in
	 * <code>varsToBeSplit</code> and one for the rest.
	 * 
	 * @param sd
	 *            the {@link SimpleDeclaration} to be split
	 * @param varsToBeSplit
	 *            a {@link Set} of {@link Variable}s that should have their own
	 *            {@link SimpleDeclaration}
	 * @return the newly created {@link SimpleDeclaration} declaring all
	 *         <code>varsToBeSplit</code>
	 */
	protected SimpleDeclaration splitSimpleDeclaration(SimpleDeclaration sd,
			Set<Variable> varsToBeSplit) {
		GreqlSyntaxGraph syntaxgraph = (GreqlSyntaxGraph) sd.getGraph();
		Set<Variable> varsDeclaredBySD = OptimizerUtility
				.collectVariablesDeclaredBy(sd);

		if (varsDeclaredBySD.size() == varsToBeSplit.size()) {
			// there's nothing to split out anymore
			return sd;
		}
		Declaration parentDecl = (Declaration) sd
				.getFirstIncidenceToIsSimpleDeclOf(Direction.VERTEX_TO_EDGE).getEdge().getOmega();
		IsSimpleDeclOf oldEdge = sd.getFirstIncidenceToIsSimpleDeclOf().getEdge();
		SimpleDeclaration newSD = syntaxgraph.createSimpleDeclaration();
		IsSimpleDeclOf newEdge = syntaxgraph.createIsSimpleDeclOf(newSD,
				parentDecl);
		syntaxgraph.createIsTypeExprOfDeclaration((Expression) sd
				.getFirstIncidenceToIsTypeExprOfDeclaration(Direction.EDGE_TO_VERTEX)
				.getEdge().getAlpha(), newSD);
		newEdge.getFirst_greql2Aggregation_GoesTo_Greql2Vertex().putAfterAtVertex(oldEdge.getFirst_greql2Aggregation_GoesTo_Greql2Vertex());

		for (Variable var : varsToBeSplit) {
			IsDeclaredVarOf_isDeclaredVarOf_omega inc = sd
					.getFirst_isDeclaredVarOf_omega();
			HashSet<IsDeclaredVarOf_isDeclaredVarOf_omega> relinkIncs = new HashSet<IsDeclaredVarOf_isDeclaredVarOf_omega>();
			while (inc != null) {
				if (inc.getEdge().getAlpha() == var) {
					// This inc is now declared by newSD, so we need to relink
					// the edge.
					relinkIncs.add(inc);
				}
				inc = inc.getNextIsDeclaredVarOf_omegaAtVertex();
			}
			for (IsDeclaredVarOf_isDeclaredVarOf_omega relinkEdge : relinkIncs) {
				relinkEdge.getEdge().setOmega(newSD);
			}
		}
		return newSD;
	}

}
