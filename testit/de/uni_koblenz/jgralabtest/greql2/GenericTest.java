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

package de.uni_koblenz.jgralabtest.greql2;

import java.io.File;
import java.util.Iterator;

import org.junit.Before;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.greql2.SerializableGreql2;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluator;
import de.uni_koblenz.jgralab.greql2.optimizer.DefaultOptimizer;
import de.uni_koblenz.jgralab.greql2.optimizer.Optimizer;
import de.uni_koblenz.jgralab.greql2.parser.GreqlParser;
import de.uni_koblenz.jgralab.utilities.tg2dot.Tg2Dot;

public class GenericTests {

	/**
	 * Print the query syntaxgraphs (unoptimized, optimized with one specific
	 * optimizer, and optimized by the default optimizer) to user.home.
	 */
	public static boolean DEBUG_SYNTAXGRAPHS = false;

	Graph graph = null;
	Graph cyclicGraph = null;
	Graph tree = null;

	protected void printTestFunctionHeader(String functionName) {
		System.out
				.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println("START     " + functionName);
		System.out
				.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}

	protected void printTestFunctionFooter(String functionName) {
		System.out
				.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		System.out.println("END       " + functionName);
		System.out
				.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	}

	private static GreqlEvaluator eval = new GreqlEvaluator((String) null,
			null, null);

	@Before
	public void setUp() throws Exception {
		eval.setVariable("nix", new JValueImpl(133));
		eval.setVariable("FOO", new JValueImpl("Currywurst"));
	}

	protected void setBoundVariable(String varName, JValue val) {
		eval.setVariable(varName, val);
	}

	protected JValue getBoundVariable(String varName) {
		return eval.getVariable(varName);
	}

	protected Graph getTestGraph() throws Exception {
		if (graph == null) {
			graph = createTestGraph();
		}
		return graph;
	}

	protected Graph getTestTree() throws Exception {
		if (tree == null) {
			tree = createTestTree();
		}
		return tree;
	}

	protected Graph getCyclicTestGraph() throws Exception {
		if (cyclicGraph == null) {
			cyclicGraph = createCyclicTestGraph();
		}
		return cyclicGraph;
	}

	protected Graph createTestGraph() throws Exception {
		// TODO: Broken, because the GReQL parser removes all WhereExpressions
		// and LetExpressions! Add some better test graph here and adapt all
		// queries...
		String query = "from i:c report i end where d:=\"nada\", c:=b, b:=a, a:=\"Mensaessen\"";
		Graph g = GreqlParser.parse(query);
		// Tg2Dot.printGraphAsDot(g, true, "/tmp/testgraph.dot");
		return g;
	}

	protected Graph createCyclicTestGraph() throws Exception {
		MinimalSchema s = MinimalSchema.instance();
		MinimalGraph g = s.createMinimalGraph(10, 10);
		Node[] v = new Node[10];
		for (int i = 0; i < 10; i++) {
			v[i] = g.createNode();
		}
		for (int i = 0; i < 10; i++) {
			g.createLink(v[i], v[(i + 1) % 10]);
		}
		return g;
	}

	protected Graph createTestTree() throws Exception {
		// create a binary tree where v[0] is the root
		MinimalSchema s = MinimalSchema.instance();
		MinimalGraph g = s.createMinimalGraph(10, 10);
		Node[] v = new Node[15];
		for (int i = 0; i < 15; i++) {
			v[i] = g.createNode();
		}
		for (int i = 0; i < (v.length - 1) / 2; i++) {
			g.createLink(v[i], v[i * 2 + 1]);
			g.createLink(v[i], v[i * 2 + 2]);
			System.out.println("[" + i + ", " + (i * 2 + 1) + ", "
					+ (i * 2 + 2) + "]");
		}
		return g;
	}

	protected JValue evalTestQuery(String functionName, String query)
			throws Exception {
		return evalTestQuery(functionName, query, null, getTestGraph());
	}

	protected JValue evalTestQuery(String functionName, String query,
			Optimizer optimizer) throws Exception {
		return evalTestQuery(functionName, query, optimizer, getTestGraph());
	}

	protected JValue evalTestQuery(String functionName, String query,
			Graph datagraph) throws Exception {
		return evalTestQuery(functionName, query, null, datagraph);
	}

	protected JValue evalTestQuery(String functionName, String query,
			Optimizer optimizer, Graph datagraph) throws Exception {
		printTestFunctionHeader(functionName);
		eval.setQuery(query);
		eval.setDatagraph(datagraph);
		eval.setUseSavedOptimizedSyntaxGraph(false);

		if (optimizer != null) {
			eval.setOptimize(true);
			eval.setOptimizer(optimizer);
		} else {
			eval.setOptimize(false);
		}

		// when optimizing turn on logging, too.
		eval.startEvaluation(eval.isOptimize(), true);

		if (DEBUG_SYNTAXGRAPHS) {
			String dotFileName = System.getProperty("user.home")
					+ File.separator;
			if (optimizer != null) {
				System.out.println("Optimized Query:");
				if (optimizer instanceof DefaultOptimizer) {
					dotFileName += "default-optimized-query.dot";
				} else {
					dotFileName += "optimized-query.dot";
				}
			} else {
				System.out.println("Unoptimized Query:");
				dotFileName += "unoptimized-query.dot";
			}
			System.out.println(((SerializableGreql2) eval.getSyntaxGraph())
					.serialize());
			Tg2Dot.printGraphAsDot(eval.getSyntaxGraph(), true, dotFileName);
		}

		printTestFunctionFooter(functionName);

		JValue result = eval.getEvaluationResult();
		eval.printEvaluationTimes();
		return result;
	}

	protected JValue getNthValue(JValueCollection col, int n) {
		Iterator<JValue> iter = col.iterator();
		int i = 0;
		while (iter.hasNext()) {
			JValue value = iter.next();
			if (i == n) {
				return value;
			}
			i++;
		}
		return null;
	}

	protected void printResult(JValue result) throws Exception {
		System.out.println("Result is: " + result);
		if (result.isCollection()) {
			System.out.println("Collection size is: "
					+ result.toCollection().size());
		}
	}

}
