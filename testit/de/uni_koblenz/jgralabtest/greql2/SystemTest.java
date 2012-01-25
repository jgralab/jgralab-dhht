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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.greql2.parser.GreqlParser;

public class SystemTest extends GenericTests {

	@Override
	protected Graph createTestGraph() throws Exception {
		int count = 1;
		String part1 = "from i:a report ";
		String part2 = " end where q:=a, l:=a, m:=a, n:=a, o:=a, p:=a, k:= a, j:=a, h := a, g := a, f:= a, e := a, d:=a, c:=a, b:=a, a:=4";
		StringBuilder queryString = new StringBuilder();
		for (int i = 0; i < count; i++) { // 65
			queryString.append(part1);
		}
		queryString
				.append(" add(i, mul( a, sub(b, mod( c, sub( d, add( e)))))) ");
		for (int i = 0; i < count; i++) {
			queryString.append(part2);
		}
		return GreqlParser.parse(queryString.toString());
	}

	@Test
	public void testFunctionArgumentsEvaluation() throws Exception {
		String queryString = "from x:V{FunctionApplication}, y:V{Expression} with x <--{IsArgumentOf} y report tup( tup(\"Function: \", x ), tup(\"Argument: \", y )) end";
		JValue result = evalTestQuery("FunctionArgumentsEvaluation",
				queryString);
		assertEquals(11, result.toCollection().size());
	}

	@Test
	public void testFunctionAsFunctionArgumentEvaluation() throws Exception {
		String queryString = "from x:V{FunctionApplication}, y:V{FunctionApplication} with x <--{IsArgumentOf} y report tup( tup(\"Function: \", x ), tup(\"Argument: \", y )) end";
		JValue result = evalTestQuery("FunctionAsFunctionArgumentEvaluation",
				queryString);
		assertEquals(5, result.toCollection().size());
	}

	@Test
	public void testFunctionAsFunctionArgumentAsFuntionArgumentEvaluation()
			throws Exception {
		String queryString = "from x:V{FunctionApplication}, y:V{FunctionApplication} with x <--{IsArgumentOf} <--{IsArgumentOf} y report tup( tup(\"Function: \", x ), tup(\"Argument: \", y )) end";
		JValue result = evalTestQuery(
				"FunctionAsFunctionAsFunctionArgumentEvaluation", queryString);
		assertEquals(4, result.toCollection().size());
	}

	@Test
	public void testFunctionAsArgumentInBagComprehensionEvaluation()
			throws Exception {
		String queryString = "from x:V{FunctionApplication}, y:V{BagComprehension} with x -->{IsArgumentOf}+ -->{IsCompResultDefOf} y report tup(tup(\"Function: \", x ), tup(\"BagComprehension: \", y )) end";
		JValue result = evalTestQuery("FunctionAsArgumentInBagComprehension",
				queryString);
		assertEquals(5, result.toCollection().size());
	}

	@Test
	public void testVariableAsVariableDefinition() throws Exception {
		// TODO: Broken, because the GReQL parser removes all WhereExpressions
		// and LetExpressions!
		String queryString = "from x:V{Variable}, y:V{Variable} with x -->{IsVarOf} <--{IsExprOf} y report x.name as \"DefinedVariable\", y.name as \"Definition\" end";
		JValue result = evalTestQuery("VariableAsVariableDefinition",
				queryString);
		assertEquals(15, result.toCollection().size());
	}

	@Test
	public void testIdentifierWithUsage() throws Exception {
		// TODO: Broken, because the GReQL parser removes all WhereExpressions
		// and LetExpressions!
		String queryString = "from x:V{Identifier} report x.name as \"Identifier\", outDegree{IsArgumentOf}(x) as \"UsageCount\", edgesFrom(x) as \"Usages\" end";
		JValue result = evalTestQuery("IdentifierWithUsage", queryString);
		assertEquals(21, result.toCollection().size());
	}

}
