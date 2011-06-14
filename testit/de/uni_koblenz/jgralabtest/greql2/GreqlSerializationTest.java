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
package de.uni_koblenz.jgralabtest.greql2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uni_koblenz.jgralabtest.greql2.testfunctions.IsPrime;

/**
 * @author Tassilo Horn &lt;horn@uni-koblenz.de&gt;
 * 
 */
public class GreqlSerializationTest {
	static {
		Greql2Schema.instance().getGraphFactory().setGraphInMemoryImplementationClass(
				Greql2.class, SerializableGreql2Impl.class);

		Greql2FunctionLibrary.instance().registerUserDefinedFunction(
				IsPrime.class);
	}

	private void check(String query) {
		Greql2 queryGraph = GreqlParser.parse(query);
		GreqlEvaluator e1 = new GreqlEvaluator(query, queryGraph, null);
		GreqlEvaluator e2 = new GreqlEvaluator(
				((SerializableGreql2) queryGraph).serialize(), queryGraph, null);

		e1.startEvaluation();
		e2.startEvaluation();

		JValue r1 = e1.getEvaluationResult();
		JValue r2 = e2.getEvaluationResult();

		assertEquals(r1, r2);
	}

	private String[] queries = {
			"from i : list(1..10) with i*i < 15 report i end",
			"forall i : list(1..100), i <> 10, i < 90 @ i * i + 1 % 4 = 17",
			"from i : from i : list(30..40) with i < 35 reportSet i end, x : list(1, 3, 17, 19) with isPrime(x+i) report i, x end",
			"from w : list(2..10), x : list(2..10), y : list(2..10), z : list(1..2) "
					+ "     with isPrime(x + z) and x * x > y and z > x * x "
					+ "     reportBag w, x, y, z end",
			"from x : list(1..5),                  "
					+ "          y : list(21..100)   "
					+ "     with isPrime(x) and isPrime(y)       "
					+ "          and isPrime(theElement(  "
					+ "                        from x : list(12..13),"
					+ "                             y : list(10..11)"
					+ "                        with isPrime(y) and isPrime(x+y)"
					+ "                        reportSet x+y end))"
					+ "     report x, y, from y : list(21..100),"
					+ "                       z : from a : list(10..20),"
					+ "                                b : list(30..60)"
					+ "                           with a * 3 = b and isPrime(a)"
					+ "                           reportSet a + b end"
					+ "                  with isPrime(y+x) and isPrime(z+x)"
					+ "                  reportSet y, z end                "
					+ "     end",
			"from class      : V,          "
					+ "          superClass : V           "
					+ "     with count(children(superClass)) > 1                "
					+ "          and superClass -->+ class "
					+ "          and (exists mid, mid2 : V,                                  "
					+ "                      mid -->+ class, "
					+ "                      mid2 -->+ class "
					+ "                      @ mid <> mid2) "
					+ "     reportSet class                              "
					+ "     end",
			"from a : V{Variable},                              "
					+ "              b : V{BagComprehension}                       "
					+ "         with a -->* b                                      "
					+ "         reportBag from x : list(1..10),                    "
					+ "                        y : list(11..20)                    "
					+ "                   with isPrime(x * x + y - 1)              "
					+ "                   reportSet x * x + y - 1, x, y, a, b end, "
					+ "                   a, b end",
			"from x:list(1..10), y:list(x..13), z:list(1..x), a : set(1, 2, 3), b : z "
					+ "           with x <> 0 and y <> 0 and z <> 0 and (b <> z)"
					+ "           report isPrime(z), isPrime(z*z), isPrime(z+z*z-1), b end",
			"from u, v, w, x, y, z : set(true, false)     "
					+ "     with ((u xor v) or (w and x and (y or z)) or (y and z) or (u and z) and ((u and x) or (y and w) and (u or v)))     "
					+ "          or ((u xor v) or (w and x and (y or z)) or (y and z) or (u and z) and ((u and x) or (y and w) and (u or v)))  "
					+ "          and ((u xor v) or (w and x and (y or z)) or (y and z) or (u and z) and ((u and x) or (y and w) and (u or v))) "
					+ "     reportSet u, v, w, x, y, z end          " };

	@Test
	public void testAll() {
		for (String q : queries) {
			check(q);
		}
	}
}
