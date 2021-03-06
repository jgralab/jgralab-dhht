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

package de.uni_koblenz.jgralabtest.greql2.testfunctions;

import java.util.ArrayList;

import org.junit.experimental.categories.Category;

import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.graphmarker.AbstractGraphMarker;

/**
 * Checks if the given number is a prime number.
 * <dl>
 * <dt><b>GReQL-signature</b></dt>
 * <dd><code>BOOL isPrime(number:LONG, noOfTestRuns:INT)</code></dd>
 * <dd><code>BOOL isPrime(number:LONG)</code></dd>
 * <dd>&nbsp;</dd>
 * </dl>
 * <dl>
 * <dt></dt>
 * <dd>
 * <dl>
 * <dt><b>Parameters:</b></dt>
 * <dd><code>number: LONG</code> - the number you want to test for primality</dd>
 * <dd><code>noOfTestRuns: INT</code> - the number of test runs (defaults to 10
 * if omitted)</dd>
 * <dt><b>Returns:</b></dt>
 * <dd><code>false</code> if <code>number</code> is no prime number. If it
 * returns <code>true</code> the probability that <code>number</code> is indeed
 * prime is at least <code>1-(1/4)^noOfTestRuns</code>.</dd>
 * <dd>&nbsp;</dd>
 * </dl>
 * </dd>
 * </dl>
 * 
 * @author ist@uni-koblenz.de
 */
public class IsPrime extends Greql2Function {
	{
		JValueType[][] x = { { JValueType.LONG, JValueType.BOOL },
				{ JValueType.LONG, JValueType.INT, JValueType.BOOL } };
		signatures = x;

		description = "Return true, if the given number is a prime number.\n"
				+ "This function performs the Miller-Rabin pseudo primality\n"
				+ "test. The optional second parameter $k$ is an integer that\n"
				+ "specifies influences the probability of being a prime.\n"
				+ "The chances of being prime is $1- (\\frac{1}{4})^k$.  The default\n"
				+ "value of $k$ is 10.";

		Category[] c = { Category.ARITHMETICS };
		categories = c;
	}

	/**
	 * The costs for an isPrime function application.
	 * 
	 * Since those depend heavily on the parameter(s) of isPrime, but those
	 * aren't available before evaluation, it's hard to set it to a "good"
	 * value...
	 */
	private static final int ESTIMATED_COSTS_PER_RUN = 5;

	/**
	 * The selectivity for isPrime. The number of prime numbers < x can be
	 * estimated with x / ln(x). So the selectivity is (x / ln(x))/x = 1/ln(x).
	 * 
	 * Since we assume that isPrime is most often called with smaller values, we
	 * use 500 for x.
	 */
	private static final double SELECTIVITY = 1.0 / Math.log(5000);

	/**
	 * @param a
	 *            a random number between 2 and <code>n-1</code>
	 * @param i
	 * @param n
	 *            the number to check the primality of
	 * @return 1 if it's possible that <code>n</code> is a prime number, or
	 *         something else if <code>n</code> is definitely composite.
	 */
	private static long witness(long a, long i, long n) {
		long x, y;
		if (i == 0) {
			return 1;
		}

		x = witness(a, i / 2, n);
		if (x == 0) {
			return 0;
		}

		y = x * x % n;
		if ((y == 1) && (x != 1) && (x != n - 1)) {
			return 0;
		}

		if (i % 2 != 0) {
			y = a * y % n;
		}
		return y;
	}

	/**
	 * @param x
	 *            lower bound
	 * @param y
	 *            higher bound
	 * @return a random number between <code>x</code> and <code>y</code>
	 */
	private static long random(long x, long y) {
		return Math.round(Math.random() * (y - x) + x);
	}

	/**
	 * @return <code>false</code>, if <code>number</code> is no prime number or
	 *         <true>true</code>, if <code>number</code> is a prime number with
	 *         a probability of <code>1-(1/number)^noOfTestRuns</code>.
	 */
	private static boolean isPrime(long number, int noOfTestRuns) {
		if (number < 2) {
			return false;
		}
		if (number == 2) {
			return true;
		}
		long w = 0;
		for (int i = 0; i < noOfTestRuns; i++) {
			w = witness(random(2, number - 1), number - 1, number);
			if (w != 1) {
				return false;
			}
		}
		return true;
	}

	@Override
	public JValue evaluate(Graph graph,
			AbstractGraphMarker<AttributedElement> subgraph, JValue[] arguments)
			throws EvaluateException {
		int noOfTestRuns = 10;
		switch (checkArguments(arguments)) {
		case 0:
			break;
		case 1:
			noOfTestRuns = arguments[1].toInteger();
			if (noOfTestRuns <= 0) {
				throw new EvaluateException(
						"isPrime's second argument must be positive!");
			}
			break;
		default:
			throw new WrongFunctionParameterException(this, arguments);
		}
		long number = arguments[0].toLong();

		if (number < 2) {
			return new JValueImpl(JValueBoolean.getFalseValue());
		}

		return new JValueImpl(isPrime(number, noOfTestRuns));
	}

	@Override
	public long getEstimatedCardinality(int inElements) {
		return 1;
	}

	@Override
	public long getEstimatedCosts(ArrayList<Long> inElements) {
		return 10 * ESTIMATED_COSTS_PER_RUN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni_koblenz.jgralab.greql2.funlib.Greql2Function#getSelectivity()
	 */
	@Override
	public double getSelectivity() {
		return SELECTIVITY;
	}
}
