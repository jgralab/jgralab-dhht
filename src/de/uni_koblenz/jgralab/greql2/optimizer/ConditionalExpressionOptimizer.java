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
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;

import de.uni_koblenz.jgralab.BinaryEdge;
import de.uni_koblenz.jgralab.Direction;
import de.uni_koblenz.jgralab.Incidence;
import de.uni_koblenz.jgralab.JGraLab;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.exception.OptimizerException;
import de.uni_koblenz.jgralab.greql2.optimizer.condexp.Formula;
import de.uni_koblenz.jgralab.greql2.schema.BoolLiteral;
import de.uni_koblenz.jgralab.greql2.schema.FunctionApplication;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Expression;
import de.uni_koblenz.jgralab.greql2.schema.Greql2Vertex;
import de.uni_koblenz.jgralab.greql2.schema.GreqlSyntaxGraph;
import de.uni_koblenz.jgralab.greql2.schema.IsConstraintOf;

/**
 * TODO: (heimdall) Comment class!
 * 
 * @author ist@uni-koblenz.de
 * 
 */
public class ConditionalExpressionOptimizer extends OptimizerBase {

	private static Logger logger = JGraLab
			.getLogger(ConditionalExpressionOptimizer.class.getPackage()
					.getName());

	private static class VertexEdgeClassTuple {
		public VertexEdgeClassTuple(Greql2Vertex v, Class<? extends BinaryEdge> ec) {
			this.v = v;
			this.ec = ec;
		}

		Greql2Vertex v;
		Class<? extends BinaryEdge> ec;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.greql2.optimizer.Optimizer#isEquivalent(de.uni_koblenz
	 * .jgralab.greql2.optimizer.Optimizer)
	 */
	@Override
	public boolean isEquivalent(Optimizer optimizer) {
		if (optimizer instanceof ConditionalExpressionOptimizer) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.uni_koblenz.jgralab.greql2.optimizer.Optimizer#optimize(de.uni_koblenz
	 * .jgralab.greql2.evaluator.GreqlEvaluator,
	 * de.uni_koblenz.jgralab.greql2.schema.Greql2)
	 */
	@Override
	public boolean optimize(GreqlEvaluator eval, GreqlSyntaxGraph syntaxgraph)
			throws OptimizerException {
		boolean simplifiedOrOptimized = false;

		FunctionApplication top = findAndOrNotFunApp(syntaxgraph
				.getFirstGreql2Expression());
		while (top != null) {
			LinkedList<VertexEdgeClassTuple> relinkables = rememberConnections(top);
			Formula formula = Formula.createFormulaFromExpression(top, eval);
			// System.out.println("Formula = " + formula);
			Formula optimizedFormula = formula.simplify().optimize();
			if (!formula.equals(optimizedFormula)) {
				simplifiedOrOptimized = true;
				logger.fine(optimizerHeaderString()
						+ "Transformed constraint\n    " + formula
						+ "\nto\n    " + optimizedFormula + ".");
				Greql2Vertex newTop = optimizedFormula.toExpression();
				for (VertexEdgeClassTuple vect : relinkables) {
					syntaxgraph.createEdge(vect.ec, newTop, vect.v);
				}
				top.delete();
				top = findAndOrNotFunApp(syntaxgraph.getFirstGreql2Expression());
			} else {
				top = null;
			}
		}

		// delete "with true" constraints
		Set<Vertex> verticesToDelete = new HashSet<Vertex>();
		for (IsConstraintOf ico : syntaxgraph.getIsConstraintOfEdges()) {
			Vertex alpha = ico.getAlpha();
			if (alpha instanceof BoolLiteral) {
				BoolLiteral bl = (BoolLiteral) alpha;
				if (bl.is_boolValue()) {
					verticesToDelete.add(bl);
				}
			}
		}
		for (Vertex bl : verticesToDelete) {
			bl.delete();
		}

		recreateVertexEvaluators(eval);
		OptimizerUtility.createMissingSourcePositions(syntaxgraph);

		// Tg2Dot.printGraphAsDot(syntaxgraph, true, "/home/horn/ceo.dot");
		// System.out.println("Afted CEO:");
		// System.out.println(((SerializableGreql2) syntaxgraph).serialize());

		return simplifiedOrOptimized;
	}

	@SuppressWarnings("unchecked")
	private LinkedList<VertexEdgeClassTuple> rememberConnections(
			FunctionApplication top) {
		LinkedList<VertexEdgeClassTuple> list = new LinkedList<VertexEdgeClassTuple>();
		assert top.isValid();
		for (Incidence i : top.getIncidences(Direction.VERTEX_TO_EDGE)) {
			list.add(new VertexEdgeClassTuple((Greql2Vertex) ((BinaryEdge) i.getEdge()).getOmega(),
					(Class<? extends BinaryEdge>) i.getEdge().getM1Class()));
		}
		return list;
	}

	private FunctionApplication findAndOrNotFunApp(Greql2Expression g) {
		Queue<Greql2Vertex> queue = new LinkedList<Greql2Vertex>();
		queue.add(g);
		while (!queue.isEmpty()) {
			Greql2Vertex v = queue.poll();
			if (v instanceof FunctionApplication) {
				FunctionApplication f = (FunctionApplication) v;
				if (OptimizerUtility.isAnd(f) || OptimizerUtility.isOr(f)
						|| OptimizerUtility.isNot(f)) {
					return f;
				}
			}
			for (Incidence i : v.getIncidences(Direction.EDGE_TO_VERTEX)) {
				queue.offer((Greql2Vertex) ((BinaryEdge) i.getEdge()).getAlpha());
			}
		}
		return null;
	}

}
